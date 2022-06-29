package markup;

import java.util.ArrayList;
import java.util.List;

public class Emphasis extends AbstractElement {
    
    public Emphasis(List<Elements> list) {
        super(list);
    }

    @Override
    public void toMarkdown(StringBuilder stringBuilder) {
        super.toMarkdown(stringBuilder, "*");
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        super.toBBCode(stringBuilder, "[i]", "[/i]");
    }
}
