package org.freenono.provider;

import java.util.List;

import org.freenono.controller.HighscoreManager;
import org.freenono.controller.Score;
import org.freenono.controller.SimpleStatistics;

/**
 * Provides tool methods for handling nonogram collections and courses.
 *
 * @author Christian Wichmann
 */
public final class CollectionTools {

    /**
     * Hide utility class constructor.
     */
    private CollectionTools() {

    }

    /**
     * Checks how much nonograms of a given course are solved and whether the course is completed.
     *
     * @param cp
     *            course provider from nonogram tree to be checked
     * @return number of unsolved nonograms or zero if course is complete
     */
    public static int countUnsolvedNonograms(final CourseProvider cp) {

        int unsolvedNonogramsInCourse = 0;

        for (final NonogramProvider np : cp.getNonogramProvider()) {
            final String hash = np.fetchNonogram().getHash();
            final String won = (String) SimpleStatistics.getInstance().getValue("won_" + hash);
            if ("0".equals(won)) {
                unsolvedNonogramsInCourse++;
            }
        }

        return unsolvedNonogramsInCourse;
    }

    /**
     * Check whether a course can be counted as complete. First it checks if the number of nonograms
     * in the course is more than zero, because random courses have zero nonograms at the beginning
     * when no seeds have been given. Then true is only returned when all nonograms in the course
     * have been solved at least one time.
     *
     * @param cp
     *            course provider for course that should be checked
     * @return true, if course has nonograms in it and all are solved
     */
    public static boolean checkIfCourseWasCompleted(final CourseProvider cp) {

        if (cp.getNumberOfNonograms() == 0) {
            return false;
        }
        if (countUnsolvedNonograms(cp) == 0) {
            return true;
        }
        return false;
    }

    /**
     * Determines when the last nonogram from a given course was played. Playing dates are stored in
     * HighscoreManager and retrieved by nonogram hash.
     *
     * @param cp
     *            course provider from nonogram tree to be checked
     * @return time when last nonogram from course was played or 0 when no nonogram was ever played.
     */
    public static long determineDateWhenLastPlayed(final CourseProvider cp) {

        long dateWhenLastPlayed = 0;
        final HighscoreManager hm = HighscoreManager.getInstance();

        for (final NonogramProvider np : cp.getNonogramProvider()) {
            /*
             * Fetching all highscores for every nonogram in course and check when the last one was
             * played. This time is returned by the method. Algorithm based on the assumption that
             * scores are returned by HighscoreManager sorted by time!
             */
            final String hash = np.fetchNonogram().getHash();
            final List<Score> list = hm.getHighscoreListForNonogram(hash);
            if (!list.isEmpty()) {
                final long currentScore = list.get(0).getTime();
                if (currentScore > dateWhenLastPlayed) {
                    dateWhenLastPlayed = currentScore;
                }
            }
        }

        return dateWhenLastPlayed;
    }
}
