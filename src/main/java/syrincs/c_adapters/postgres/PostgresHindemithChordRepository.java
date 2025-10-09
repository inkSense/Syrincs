package syrincs.c_adapters.postgres;

import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.b_application.ports.HindemithChordRepositoryPort;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Postgres implementation of the Hindemith chord repository.
 *
 * Notes about Clean Architecture placement:
 * - This class is an outer-layer adapter (c_adapters). It depends on JDBC and Postgres types.
 * - It maps between DB rows (hindemithChords table) and the domain entity HindemithChord.
 */
public class PostgresHindemithChordRepository implements HindemithChordRepositoryPort {

    private final String url;
    private final String user;
    private final String password;

    public PostgresHindemithChordRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Creates the table if it does not exist. Call this during bootstrap if desired.
     */
    public void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS public.hindemithChords (
                    id          SERIAL PRIMARY KEY,
                    notes       INT[] NOT NULL,
                    numNotes    INT NOT NULL,
                    minNote     INT NOT NULL,
                    maxNote     INT NOT NULL,
                    rootNote    INT,
                    chordGroup  INT
                )
                """;
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to ensure table exists", e);
        }
    }

    @Override
    public long save(HindemithChord chord) {
        String sql = "INSERT INTO public.hindemithChords (notes, numNotes, minNote, maxNote, rootNote, chordGroup) VALUES (?,?,?,?,?,?) RETURNING id";
        List<Integer> notes = chord.getNotes();
        int numNotes = notes.size();
        int min = notes.stream().mapToInt(Integer::intValue).min().orElseThrow();
        int max = notes.stream().mapToInt(Integer::intValue).max().orElseThrow();
        Integer root = chord.getRootNote();
        Integer group = chord.getGroup();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            Array arr = con.createArrayOf("int4", notes.stream().map(Integer::valueOf).toArray(Integer[]::new));
            ps.setArray(1, arr);
            ps.setInt(2, numNotes);
            ps.setInt(3, min);
            ps.setInt(4, max);
            if (root == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, root);
            if (group == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, group);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new SQLException("INSERT did not return id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save HindemithChord", e);
        }
    }

    @Override
    public Optional<HindemithChord> findById(long id) {
        String sql = "SELECT notes, rootNote, chordGroup FROM public.hindemithChords WHERE id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Array arr = rs.getArray("notes");
                Integer[] noteArray = (Integer[]) arr.getArray();
                List<Integer> notes = new ArrayList<>(Arrays.asList(noteArray));
                int r = rs.getInt("rootNote");
                int g = rs.getInt("chordGroup");
                HindemithChord chord = new HindemithChord(notes, r, g);
                return Optional.of(chord);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load HindemithChord by id", e);
        }
    }

    @Override
    public List<HindemithChord> findAll() {
        String sql = "SELECT notes, rootNote, chordGroup FROM public.hindemithChords ORDER BY id";
        List<HindemithChord> result = new ArrayList<>();
        try (Connection con = getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Array arr = rs.getArray("notes");
                Integer[] noteArray = (Integer[]) arr.getArray();
                List<Integer> notes = new ArrayList<>(Arrays.asList(noteArray));
                int r = rs.getInt("rootNote");
                int g = rs.getInt("chordGroup");
                HindemithChord chord = new HindemithChord(notes, r, g);
                result.add(chord);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load all HindemithChords", e);
        }
    }

    @Override
    public List<HindemithChord> getAllOf(Integer group) {
        String sql = "SELECT notes, rootNote, chordGroup FROM public.hindemithChords WHERE chordGroup = ? ORDER BY id";

        List<HindemithChord> result = new ArrayList<>();
        try (Connection con = getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, group);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Array arr = rs.getArray("notes");
                        Integer[] noteArray = (Integer[]) arr.getArray();
                        List<Integer> notes = new ArrayList<>(Arrays.asList(noteArray));
                        int r = rs.getInt("rootNote");
                        int g = rs.getInt("chordGroup");

                        HindemithChord chord = new HindemithChord(notes, r, g);
                        result.add(chord);
                    }
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load HindemithChords for group=" + group, e);
        }
    }

    @Override
    public List<HindemithChord> getAllOfRootNote(Integer rootNote){
        String sqlEq = "SELECT notes, rootNote , chordGroup FROM public.hindemithChords WHERE rootNote = ? ORDER BY id";
        List<HindemithChord> result = new ArrayList<>();
        try (Connection con = getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sqlEq)) {
                ps.setInt(1, rootNote);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Array arr = rs.getArray("notes");
                        Integer[] noteArray = (Integer[]) arr.getArray();
                        List<Integer> notes = new ArrayList<>(Arrays.asList(noteArray));
                        int r = rs.getInt("rootNote");
                        int g = rs.getInt("chordGroup");

                        HindemithChord chord = new HindemithChord(notes, r, g);
                        result.add(chord);
                    }
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load HindemithChords for rootNote=" + rootNote, e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM public.hindemithChords WHERE id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete HindemithChord by id", e);
        }
    }
}
