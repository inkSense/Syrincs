package syrincs.b_application;

import syrincs.a_domain.hindemith.HindemithChord;
import syrincs.b_application.ports.HindemithChordRepositoryPort;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Simple in-memory fake for tests. Not thread-safe and minimal.
 */
class FakeHindemithChordRepository implements HindemithChordRepositoryPort {
    private final Map<Long, HindemithChord> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public long save(HindemithChord chord) {
        long id = seq.getAndIncrement();
        store.put(id, chord);
        return id;
    }

    @Override
    public List<Long> saveAll(List<HindemithChord> chords) {
        List<Long> ids = new ArrayList<>(chords.size());
        for (HindemithChord c : chords) {
            ids.add(save(c));
        }
        return ids;
    }

    @Override
    public void truncate() {
        store.clear();
        seq.set(1);
    }

    @Override
    public Optional<HindemithChord> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<HindemithChord> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(long id) {
        store.remove(id);
    }

    @Override
    public List<HindemithChord> getAllOf(Integer group) {
        return store.values().stream()
                .filter(c -> c.getGroup() != null && Objects.equals(c.getGroup(), group))
                .collect(Collectors.toList());
    }

    @Override
    public List<HindemithChord> getAllOfRootNote(Integer rootNote) {
        return store.values().stream()
                .filter(c -> c.getRootNote() != null && Objects.equals(c.getRootNote(), rootNote))
                .collect(Collectors.toList());
    }

    @Override
    public List<HindemithChord> getAllOfRootNoteAndGroup(Integer rootNote, Integer group) {
        return store.values().stream()
                .filter(c -> c.getRootNote() != null && Objects.equals(c.getRootNote(), rootNote))
                .filter(c -> c.getGroup() != null && Objects.equals(c.getGroup(), group))
                .collect(Collectors.toList());
    }

    @Override
    public List<HindemithChord> getAllOfRootNoteAndMaxGroup(Integer rootNote, Integer maxGroup) {
        return store.values().stream()
                .filter(c -> c.getRootNote() != null && Objects.equals(c.getRootNote(), rootNote))
                .filter(c -> c.getGroup() != null && c.getGroup() <= maxGroup)
                .collect(Collectors.toList());
    }

    @Override
    public List<HindemithChord> findByRootNoteAndGroupsAndNumNotes(int rootNote,
                                                                    Collection<Integer> groups,
                                                                    Collection<Integer> numNotes) {
        if (groups == null || numNotes == null || groups.isEmpty() || numNotes.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> groupSet = new HashSet<>(groups);
        Set<Integer> sizeSet = new HashSet<>(numNotes);
        return store.values().stream()
                .filter(c -> c.getRootNote() != null && c.getRootNote() == rootNote)
                .filter(c -> c.getGroup() != null && groupSet.contains(c.getGroup()))
                .filter(c -> c.getNotes() != null && sizeSet.contains(c.getNotes().size()))
                .collect(Collectors.toList());
    }

    @Override
    public List<HindemithChord> findByRootNoteAndGroupsAndNumNotesAndRange(int rootNote,
                                                                            Collection<Integer> groups,
                                                                            Collection<Integer> numNotes,
                                                                            int range) {
        if (groups == null || numNotes == null || groups.isEmpty() || numNotes.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> groupSet = new HashSet<>(groups);
        Set<Integer> sizeSet = new HashSet<>(numNotes);
        return store.values().stream()
                .filter(c -> c.getRootNote() != null && c.getRootNote() == rootNote)
                .filter(c -> c.getGroup() != null && groupSet.contains(c.getGroup()))
                .filter(c -> c.getNotes() != null && sizeSet.contains(c.getNotes().size()))
                .filter(c -> {
                    List<Integer> notes = c.getNotes();
                    if (notes == null || notes.isEmpty()) return false;
                    int min = notes.stream().mapToInt(Integer::intValue).min().orElse(Integer.MAX_VALUE);
                    int max = notes.stream().mapToInt(Integer::intValue).max().orElse(Integer.MIN_VALUE);
                    return (max - min) <= range;
                })
                .collect(Collectors.toList());
    }

    // Test helpers
    long put(HindemithChord chord) { return save(chord); }
}
