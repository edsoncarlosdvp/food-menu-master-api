package br.com.inovationtech.FoodMenuMasterApi.Entity;

import lombok.Getter;

@Getter
public enum CategoryItem {
    
    // Comidas
    ENTRADA("Entrada", "Pratos para começar a refeição"),
    PRATO_PRINCIPAL("Prato Principal", "Pratos principais da refeição"),
    SOBREMESA("Sobremesa", "Doces e sobremesas"),
    LANCHE("Lanche", "Lanches e petiscos"),
    PIZZA("Pizza", "Pizzas diversas"),
    MASSA("Massa", "Pratos de massa como macarrão e lasanha"),
    SALADA("Salada", "Saladas e pratos vegetarianos"),
    
    // Bebidas
    BEBIDA_ALCOOLICA("Bebida Alcoólica", "Cervejas, vinhos, drinks e destilados"),
    BEBIDA_NAO_ALCOOLICA("Bebida Não Alcoólica", "Refrigerantes, sucos e águas"),
    CAFE("Café", "Cafés, cappuccinos e bebidas quentes"),
    CHA("Chá", "Chás e infusões"),
    SUCO_NATURAL("Suco Natural", "Sucos naturais e vitaminas"),
    
    // Especiais
    PROMOCAO("Promoção", "Itens em promoção especial"),
    COMBO("Combo", "Combinações de pratos e bebidas"),
    ESPECIAL_DA_CASA("Especial da Casa", "Especialidades exclusivas do estabelecimento");
    
    private final String name;
    private final String description;
    
    CategoryItem(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Método para buscar categoria por name (útil para conversões)
    public static CategoryItem fromNome(String name) {
        for (CategoryItem category : values()) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Categoria não encontrada: " + name);
    }
}
