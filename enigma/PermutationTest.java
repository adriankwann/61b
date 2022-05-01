package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Adrian Kwan
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void permutationTest1() {
        String s = NAVALA_MAP.get("I");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALA.get("I"), newA);
        checkPerm("test1", UPPER_STRING, s);
    }
    @Test
    public void permutationTestB1() {
        String s = NAVALB_MAP.get("I");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALB.get("I"), newA);
        checkPerm("testB1", UPPER_STRING, s);
    }
    @Test
    public void permutationTestZ1() {
        String s = NAVALZ_MAP.get("I");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALZ.get("I"), newA);
        checkPerm("testZ1", UPPER_STRING, s);
    }
    @Test
    public void permutationTest2() {
        String s = NAVALA_MAP.get("II");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALA.get("II"), newA);
        checkPerm("test2", UPPER_STRING, s);
    }
    @Test
    public void permutationTest3() {
        String s = NAVALA_MAP.get("III");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALA.get("III"), newA);
        checkPerm("test3", UPPER_STRING, s);
    }
    @Test
    public void permutationTest4() {
        String s = NAVALA_MAP.get("IV");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALA.get("IV"), newA);
        checkPerm("test4", UPPER_STRING, s);
    }
    @Test
    public void permutationTest5() {
        String s = NAVALA_MAP.get("V");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALA.get("V"), newA);
        checkPerm("test5", UPPER_STRING, s);
    }
    @Test
    public void permutationTest6() {
        String s = NAVALA_MAP.get("VI");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALA.get("VI"), newA);
        checkPerm("test6", UPPER_STRING, s);
    }
    @Test
    public void permutationTest7() {
        String s = NAVALA_MAP.get("VII");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALA.get("VII"), newA);
        checkPerm("test7", UPPER_STRING, s);
    }
    @Test
    public void permutationTest8() {
        String s = NAVALA_MAP.get("VIII");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALA.get("VIII"), newA);
        checkPerm("test8", UPPER_STRING, s);
    }
    @Test
    public void permutationTestBeta() {
        String s = NAVALA_MAP.get("Beta");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALA.get("Beta"), newA);
        checkPerm("testBeta", UPPER_STRING, s);
    }
    @Test
    public void permutationTestGamma() {
        String s = NAVALA_MAP.get("Gamma");
        Alphabet newA = new Alphabet(s);
        perm = new Permutation(NAVALA.get("Gamma"), newA);
        checkPerm("testGamma", UPPER_STRING, s);
    }



}
