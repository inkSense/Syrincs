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
        withSqlVoid("Failed to ensure table exists", () -> {
            try (Connection con = getConnection(); Statement st = con.createStatement()) {
                st.execute(sql);
            }
        });
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

        return withSql("Failed to save HindemithChord", () -> {
            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                Array arr = con.createArrayOf("int4", notes.stream().map(Integer::valueOf).toArray(Integer[]::new));
                ps.setArray(1, arr);
                ps.setInt(2, numNotes);
                ps.setInt(3, min);
                ps.setInt(4, max);
                setNullableInt(ps, 5, root);
                setNullableInt(ps, 6, group);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                    throw new SQLException("INSERT did not return id");
                }
            }
        });
    }

    @Override
    public Optional<HindemithChord> findById(long id) {
        String sql = "SELECT notes, rootNote, chordGroup FROM public.hindemithChords WHERE id = ?";
        return withSql("Failed to load HindemithChord by id", () -> {
            try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return Optional.empty();
                    return Optional.of(mapRow(rs));
                }
            }
        });
    }

    @Override
    public List<HindemithChord> findAll() {
        String sql = "SELECT notes, rootNote, chordGroup FROM public.hindemithChords ORDER BY id";
        return withSql("Failed to load all HindemithChords", () -> trySql(sql));
    }

    @Override
    public List<HindemithChord> getAllOf(Integer group) {
        String sql = "SELECT notes, rootNote, chordGroup FROM public.hindemithChords WHERE chordGroup = ? ORDER BY id";
        return withSql("Failed to load HindemithChords for group=" + group, () -> trySql(sql, group));
    }

    @Override
    public List<HindemithChord> getAllOfRootNote(Integer rootNote){
        String sqlEq = "SELECT notes, rootNote , chordGroup FROM public.hindemithChords WHERE rootNote = ? ORDER BY id";
        return withSql("Failed to load HindemithChords for rootNote=" + rootNote, () -> trySql(sqlEq, rootNote));
    }

    public List<HindemithChord> getAllOfRootNoteAndGroup(Integer rootNote, Integer group){
        String sql = "SELECT notes, rootNote , chordGroup FROM public.hindemithChords WHERE rootNote = ? AND chordGroup = ? ORDER BY id";
        return withSql("Failed to load HindemithChords for rootNote=" + rootNote, () -> trySql(sql, rootNote, group));
    }

    public List<HindemithChord> getAllOfRootNoteAndMaxGroup(Integer rootNote, Integer group){
        String sql = "SELECT notes, rootNote , chordGroup FROM public.hindemithChords WHERE rootNote = ? AND chordGroup <= ? ORDER BY id";
        return withSql("Failed to load HindemithChords for rootNote=" + rootNote, () -> trySql(sql, rootNote, group));
    }


    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM public.hindemithChords WHERE id = ?";
        withSqlVoid("Failed to delete HindemithChord by id", () -> {
            try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        });
    }

    private HindemithChord mapRow(ResultSet rs) throws SQLException {
        Array arr = rs.getArray("notes");
        Integer[] noteArray = (Integer[]) arr.getArray();
        List<Integer> notes = new ArrayList<>(Arrays.asList(noteArray));
        int r = rs.getInt("rootNote");
        int g = rs.getInt("chordGroup");
        return new HindemithChord(notes, r, g);
    }

    private static void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    // SQL exception encapsulation helpers
    @FunctionalInterface
    private interface SqlCallable<T> {
        T call() throws SQLException;
    }

    @FunctionalInterface
    private interface SqlRunnable {
        void run() throws SQLException;
    }

    private <T> T withSql(String errMsg, SqlCallable<T> action) {
        try {
            return action.call();
        } catch (SQLException e) {
            throw new RuntimeException(errMsg, e);
        }
    }

    private void withSqlVoid(String errMsg, SqlRunnable action) {
        try {
            action.run();
        } catch (SQLException e) {
            throw new RuntimeException(errMsg, e);
        }
    }

    // Overloaded SQL helpers: execute a query and map rows to HindemithChord
    private List<HindemithChord> trySql(String sql) throws SQLException {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            return executeQuery(ps);
        }
    }

    private List<HindemithChord> trySql(String sql, Integer p1) throws SQLException {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            setNullableInt(ps, 1, p1);
            return executeQuery(ps);
        }
    }

    private List<HindemithChord> trySql(String sql, Integer p1, Integer p2) throws SQLException {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            setNullableInt(ps, 1, p1);
            setNullableInt(ps, 2, p2);
            return executeQuery(ps);
        }
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
