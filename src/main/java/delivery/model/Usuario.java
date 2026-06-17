package delivery.model;

import delivery.tier.TierUsuario;

public class Usuario {
    private long id;
    private String nombre, email, telefono;
    private TierUsuario tier;

    public Usuario(long id, String nombre, String email, String telefono, TierUsuario tier) {
        this.id = id; this.nombre = nombre;
        this.email = email; this.telefono = telefono;
        this.tier = tier;
    }

    public long getId()        { return id; }
    public String getNombre()  { return nombre; }
    public String getEmail()   { return email; }
    public TierUsuario getTier() { return tier; }

    @Override public String toString() { return nombre + " (" + email + ") [" + tier.getNombre() + "]"; }
}
