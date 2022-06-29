package markup;

import java.util.ArrayList;
import java.util.List;

public class Strong extends AbstractElement {
    
    public Strong(List<Elements> list) {
        super(list);
    }

    @Override
    public void toMarkdown(StringBuilder stringBuilder) {
        super.toMarkdown(stringBuilder, "__");
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        super.toBBCode(stringBuilder, "[b]", "[/b]");
    }
}
