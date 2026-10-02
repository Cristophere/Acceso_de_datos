public class Pelicula{
    private int id;
    private String titulo;
    private String director;
    private int any;
    private String genero;

    public Pelicula (int id, String titulo, String director, int any, String genero) {
        this.id = id;
        this.titulo = titulo;
        this.director = director;
        this.any = any;
        this.genero = genero; 
    }
    public int getId() { 
        return id; 
    }
    public String getTitulo() { 
        return titulo; 
    }
    public String getDirector() { 
        return director; 
    }
    public int getAny() { 
        return any; 
    }
    public String getGenero() { 
        return genero; 
    }


@Override
    public String toString() {
        return id + " | " + "titulo: " + titulo + " | " +
        "director: " + director + " | " + "año: " + any + " | " + "genero: " + genero;
    }
}
