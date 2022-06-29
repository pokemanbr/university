package markup;

import java.util.ArrayList;
import java.util.List;

public class UnorderedList extends AbstractList {
    
    public UnorderedList(List<ListItem> list) {
        super(list);
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        super.toBBCode(stringBuilder, "[list]", "[/list]");
    }
}
