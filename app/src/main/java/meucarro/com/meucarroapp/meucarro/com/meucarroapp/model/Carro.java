package meucarro.com.meucarroapp.meucarro.com.meucarroapp.model;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import meucarro.com.meucarroapp.R;

public class Carro {

    private Integer id;
    private String placa;
    private String desc;
    private Integer hodometro;
    private Integer ultima_troca_oleo;
    private Double lat;
    private Double lng;
    private Boolean alarme;

    public Carro(JSONObject json) throws JSONException {
        //nao internacionalizar, o ws é em pt-br
        this.id = json.getInt("id");
        this.placa = json.getString("placa");
        this.desc = json.getString("desc");
        this.hodometro = json.getInt("hodometro");
        this.ultima_troca_oleo = json.getInt("ultima_troca_oleo");
        this.lat = json.getDouble("lat");
        this.lng = json.getDouble("lng");
        this.alarme = json.getBoolean("alarme");
    }

    public String getHeaderString() {
        return this.placa.toString() + " - " + this.desc.toString();
    }

    public ArrayList<String> getItensArray(Context ctx) {
        ArrayList<String> itens = new ArrayList<String>();
        itens.add(ctx.getString(R.string.field_id) + this.id.toString());
        itens.add(ctx.getString(R.string.field_odometer) + String.valueOf(this.hodometro));
        itens.add(ctx.getString(R.string.field_oil_change_in) + this.ultima_troca_oleo.toString() + "\n" + ctx.getString(R.string.field_oil_change_missing) + String.valueOf(5000 - (this.hodometro - this.ultima_troca_oleo)) + ctx.getString(R.string.field_oil_change_km));
        itens.add(ctx.getString(R.string.field_alarm) + this.alarme.toString());
        itens.add(ctx.getString(R.string.field_show_in_map));
        return itens;
    }

    public Date getUltimoStatus() {
        return ultimoStatus;
    }

    public void setUltimoStatus(Date ultimoStatus) {
        this.ultimoStatus = ultimoStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getHodometro() {
        return hodometro;
    }

    public void setHodometro(int hodometro) {
        this.hodometro = hodometro;
    }

    public int getUltima_troca_oleo() {
        return ultima_troca_oleo;
    }

    public void setUltima_troca_oleo(int ultima_troca_oleo) {
        this.ultima_troca_oleo = ultima_troca_oleo;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isAlarme() {
        return alarme;
    }

    public void setAlarme(boolean alarme) {
        this.alarme = alarme;
    }

    private Date ultimoStatus;

}
