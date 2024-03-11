package com.example.benergy

class Exercicio(
    private var number: Int,
    private var nome: String,
    private var medida: String,
    private var tipoMedida: String
) {

    fun getNome() : String{
        return nome
    }

    fun getMedida() : String{
        return medida
    }

    fun getTipoMedida() : String{
        return tipoMedida
    }

    fun getNumber() : Int{
        return number
    }

    fun setNome(nome : String){
        this.nome = nome
    }

    fun setMedida(medida : String){
        this.medida = medida
    }

    fun setTipoMedida(tipoMedida : String){
        this.tipoMedida = tipoMedida
    }

    fun setNumber(number : Int){
        this.number = number
    }

    override fun toString(): String {
        return "Exercicio $number: $nome - $medida $tipoMedida"
    }

}