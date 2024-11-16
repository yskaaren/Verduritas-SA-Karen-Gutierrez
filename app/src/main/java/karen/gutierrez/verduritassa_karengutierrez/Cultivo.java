package karen.gutierrez.verduritassa_karengutierrez;  // Asegúrate de que este sea el paquete correcto

public class Cultivo {
    private String id;
    private String nombre;
    private String fecha;
    private String fechaCosecha; // Fecha de cosecha

    // Constructor
    public Cultivo(String id, String nombre, String fecha, String fechaCosecha) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.fechaCosecha = fechaCosecha;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getFechaCosecha() {
        return fechaCosecha;
    }

    // Setters (opcional)
    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setFechaCosecha(String fechaCosecha) {
        this.fechaCosecha = fechaCosecha;
    }
}

