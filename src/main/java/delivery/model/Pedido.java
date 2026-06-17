package delivery.model;

import java.util.List;

public class Pedido {
    private long id;
    private String estado, metodoEnvio;
    private double costoEnvio;
    private Usuario usuario;
    private Repartidor repartidor;   
    private List<Producto> productos;

    public Pedido(long id, String estado, String metodoEnvio, double costoEnvio,
                  Usuario usuario, Repartidor repartidor, List<Producto> productos) {
        this.id = id; this.estado = estado; this.metodoEnvio = metodoEnvio;
        this.costoEnvio = costoEnvio; this.usuario = usuario;
        this.repartidor = repartidor; this.productos = productos;
    }

    public long getId()              { return id; }
    public String getEstado()        { return estado; }
    public void setEstado(String e)  { this.estado = e; }
    public String getMetodoEnvio()   { return metodoEnvio; }
    public double getCostoEnvio()    { return costoEnvio; }
    public Usuario getUsuario()      { return usuario; }
    public Repartidor getRepartidor(){ return repartidor; }
    public void setRepartidor(Repartidor r) { this.repartidor = r; }
    public List<Producto> getProductos()    { return productos; }

    public double getTotal() {
        double subtotal = productos.stream().mapToDouble(Producto::getPrecio).sum();
        double descSubtotal = usuario.getTier() != null ? usuario.getTier().calcularDescuentoSubtotal(subtotal) : 0;
        double descEnvio = usuario.getTier() != null ? usuario.getTier().calcularDescuentoEnvio(costoEnvio) : 0;
        return (subtotal - descSubtotal) + (costoEnvio - descEnvio);
    }

    @Override public String toString() {
        String rep = repartidor != null ? repartidor.getNombre() : "sin asignar";
        return String.format("Pedido #%d | %s | Envío: %s | Total: $%.0f | %s | Rep: %s",
                id, estado, metodoEnvio, getTotal(), usuario.getNombre(), rep);
    }
}
