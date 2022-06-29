package markup;

import java.util.ArrayList;
import java.util.List;

public class OrderedList extends AbstractList {
    
    public OrderedList(List<ListItem> list) {
        super(list);
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        super.toBBCode(stringBuilder, "[list=1]", "[/list]");
    }
}
