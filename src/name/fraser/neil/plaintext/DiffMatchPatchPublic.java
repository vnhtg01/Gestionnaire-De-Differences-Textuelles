package name.fraser.neil.plaintext;

import java.util.LinkedList;

public class DiffMatchPatchPublic extends diff_match_patch {
    public LinkedList<Diff> diffLineModePublic(String text1, String text2, long deadline) {
        return diff_lineMode(text1, text2, deadline);
    }
}