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
     * Checks how much nonograms of a given course are solved and whether the
     * course is completed.
     * 
     * @param cp
     *            course provider from nonogram tree to be checked
     * @return number of unsolved nonograms or zero if course is complete
     */
    public static int countUnsolvedNonograms(final CourseProvider cp) {

        int unsolvedNonogramsInCourse = 0;

        for (NonogramProvider np : cp.getNonogramProvider()) {
            String hash = np.fetchNonogram().getHash();
            String won = SimpleStatistics.getInstance().getValue("won_" + hash);
            if ("0".equals(won)) {
                unsolvedNonogramsInCourse++;
            }
        }

        return unsolvedNonogramsInCourse;
    }

    /**
     * Determines when the last nonogram from a given course was played. Playing
     * dates are stored in HighscoreManager and retrieved by nonogram hash.
     * 
     * @param cp
     *            course provider from nonogram tree to be checked
     * @return time when last nonogram from course was played or 0 when no
     *         nonogram was ever played.
     */
    public static long determineDateWhenLastPlayed(final CourseProvider cp) {

        long dateWhenLastPlayed = 0;
        HighscoreManager hm = HighscoreManager.getInstance();

        for (NonogramProvider np : cp.getNonogramProvider()) {
            /*
             * Fetching all highscores for every nonogram in course and check
             * when the last one was played. This time is returned by the
             * method. Algorithm based on the assumption that scores are
             * returned by HighscoreManager sorted by time!
             */
            String hash = np.fetchNonogram().getHash();
            List<Score> list = hm.getHighscoreListForNonogram(hash);
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
