package markup;

import java.util.ArrayList;
import java.util.List;

public class Strikeout extends AbstractElement {
    
    public Strikeout(List<Elements> list) {
        super(list);
    }

    @Override
    public void toMarkdown(StringBuilder stringBuilder) {
        super.toMarkdown(stringBuilder, "~");
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        super.toBBCode(stringBuilder, "[s]", "[/s]");
    }
}
