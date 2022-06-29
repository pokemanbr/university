package markup;

import java.util.ArrayList;
import java.util.List;

public class Paragraph implements ParagraphList {

    private List<Elements> list;

    public Paragraph(List<Elements> list) {
        this.list = list;
    }

    public void toMarkdown(StringBuilder stringBuilder) {
        for (Elements text : list) {       
            text.toMarkdown(stringBuilder);
        }
    }

    public void toBBCode(StringBuilder stringBuilder) {
        for (Elements text : list) {       
            text.toBBCode(stringBuilder);
        }
    }
}
