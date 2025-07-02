package br.com.inovationtech.FoodMenuMasterApi.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.inovationtech.FoodMenuMasterApi.Entity.CategoryItem;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemMenuDTO {
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;
    
    @NotNull(message = "Categoria é obrigatória")
    private CategoryItem category;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Preço deve ter no máximo 8 dígitos inteiros e 2 decimais")
    private BigDecimal price;
    
    @DecimalMin(value = "0.0", message = "Avaliação deve ser maior ou igual a zero")
    @DecimalMax(value = "5.0", message = "Avaliação deve ser menor ou igual a 5")
    @Digits(integer = 1, fraction = 1, message = "Avaliação deve ter no máximo 1 dígito inteiro e 1 decimal")
    private BigDecimal rating;
    
    private Boolean active = true;
    
    @Size(max = 255, message = "Código QR deve ter no máximo 255 caracteres")
    private String qrCode;
    
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    
    // Método auxiliar para formatação do preço
    public String getPriceFormatted() {
        return this.price != null ? String.format("R$ %.2f", this.price) : "R$ 0,00";
    }
    
    // Método auxiliar para nome da categoria
    public String getCategoryName() {
        return this.category != null ? this.category.getName() : "";
    }
    
    // Método auxiliar para status de ativo
    public String getActiveStatus() {
        return (this.active != null && this.active) ? "Ativo" : "Inativo";
    }
}
