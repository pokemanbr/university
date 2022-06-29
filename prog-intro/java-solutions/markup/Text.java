package markup;

public class Text implements Elements {
    
    private String text;

    public Text(String s) {
        text = s;
    }

    @Override
    public void toMarkdown(StringBuilder stringBuilder) {
        stringBuilder.append(text);
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        stringBuilder.append(text);
    }
}
