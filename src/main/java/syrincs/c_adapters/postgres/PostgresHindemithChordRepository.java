package syrincs.c_adapters.postgres;

import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.b_application.ports.HindemithChordRepositoryPort;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private final Logger LOGGER = Logger.getLogger(PostgresHindemithChordRepository.class.getName());

    public PostgresHindemithChordRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
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
            ps.setInt(5, root);
            ps.setInt(6, group);

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
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load HindemithChord by id", e);
        }
    }

    @Override
    public List<HindemithChord> findAll() {
        String sql = "SELECT notes, rootNote, chordGroup FROM public.hindemithChords ORDER BY id";
        return trySql(sql);
    }

    @Override
    public List<HindemithChord> getAllOf(Integer group) {
        String sql = "SELECT notes, rootNote, chordGroup FROM public.hindemithChords WHERE chordGroup = ? ORDER BY id";
        return trySql(sql, group);
    }

    @Override
    public List<HindemithChord> getAllOfRootNote(Integer rootNote){
        String sql = "SELECT notes, rootNote , chordGroup FROM public.hindemithChords WHERE rootNote = ? ORDER BY id";
        return trySql(sql, rootNote);
    }

    public List<HindemithChord> getAllOfRootNoteAndGroup(Integer rootNote, Integer group){
        String sql = "SELECT notes, rootNote , chordGroup FROM public.hindemithChords WHERE rootNote = ? AND chordGroup = ? ORDER BY id";
        return trySql(sql, rootNote, group);

    }

    public List<HindemithChord> getAllOfRootNoteAndMaxGroup(Integer rootNote, Integer group){
        String sql = "SELECT notes, rootNote , chordGroup FROM public.hindemithChords WHERE rootNote = ? AND chordGroup <= ? ORDER BY id";
        return trySql(sql, rootNote, group);
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

    private HindemithChord mapRow(ResultSet rs) throws SQLException {
        Array arr = rs.getArray("notes");
        Integer[] noteArray = (Integer[]) arr.getArray();
        List<Integer> notes = new ArrayList<>(Arrays.asList(noteArray));
        int r = rs.getInt("rootNote");
        int g = rs.getInt("chordGroup");
        return new HindemithChord(notes, r, g);
    }


    // Overloaded SQL helpers: execute a query and map rows to HindemithChord
    private List<HindemithChord> trySql(String sql) {
        List<HindemithChord> chords = new ArrayList<>();
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            chords = executeQuery(ps);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to load HindemithChords", e);
        }
        return chords;
    }

    private List<HindemithChord> trySql(String sql, Integer p1) {
        List<HindemithChord> chords = new ArrayList<>();
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p1);
            chords = executeQuery(ps);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to load HindemithChords", e);
        }
        return chords;
    }

    private List<HindemithChord> trySql(String sql, Integer p1, Integer p2) {
        List<HindemithChord> chords = new ArrayList<>();
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p1);
            ps.setInt(2, p2);
            chords = executeQuery(ps);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to load HindemithChords", e);
        }
        return chords;
    }

    private List<HindemithChord> executeQuery(PreparedStatement ps) throws SQLException {
        List<HindemithChord> result = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }
}
