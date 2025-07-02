package br.com.inovationtech.FoodMenuMasterApi.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "itens_menu")
public class ItemMenuEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotNull(message = "Categoria é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoryItem category;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String description;
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Preço deve ter no máximo 8 dígitos inteiros e 2 decimais")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @DecimalMin(value = "0.0", message = "Avaliação deve ser maior ou igual a zero")
    @DecimalMax(value = "5.0", message = "Avaliação deve ser menor ou igual a 5")
    @Digits(integer = 1, fraction = 1, message = "Avaliação deve ter no máximo 1 dígito inteiro e 1 decimal")
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;
    
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    @Size(max = 255, message = "Código QR deve ter no máximo 255 caracteres")
    @Column(name = "qr_code", length = 255)
    private String qrCode;
    
    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;
    
    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;
    
    // Método auxiliar para verificar se o item está ativo
    public boolean isActive() {
        return this.active != null && this.active;
    }
    
    // Método para formatar o preço como string
    public String getPriceFormatted() {
        return String.format("R$ %.2f", this.price);
    }
}