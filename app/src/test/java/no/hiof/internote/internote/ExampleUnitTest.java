package no.hiof.internote.internote;

import org.junit.Test;

import no.hiof.internote.internote.model.NoteOverview;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void shortenTitle_isCorrect(){
        NoteOverview noteOverview = new NoteOverview("LangTittelDette", 1542648789109L, "");
        assertEquals("LangTitte...", noteOverview.getTitleShort());
    }
}