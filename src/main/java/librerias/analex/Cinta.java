package librerias.analex;

public class Cinta {
    public static final char EOF = 0;

    private String cinta;
    private int i;

    public Cinta(String cinta){
        this.cinta = cinta != null ? cinta : "";  // Asegura que la cinta nunca sea null
        this.i = 0;
    }

    public void forward(){
        if(i < cinta.length()){
            i++;
        }
    }

    public char CC(){
        return i < cinta.length() ? cinta.charAt(i) : EOF;
    }

    public void init(){
        i = 0;
    }
}
