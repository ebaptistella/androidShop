package br.edu.unoesc.shoplist.model;

/**
 * Created by roberson.alves on 29/07/2015.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "usuario")
public class Usuario extends BaseModel {
    @DatabaseField(generatedId = true)
    private Integer codigo;
    @DatabaseField
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Usuario() {
        // ORMLite precisa de um construtor sem parâmetros
    }

    public Usuario(String nome) {
        this.nome = nome;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

}
