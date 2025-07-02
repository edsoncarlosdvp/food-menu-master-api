package br.com.inovationtech.FoodMenuMasterApi.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.inovationtech.FoodMenuMasterApi.Entity.CategoryItem;
import br.com.inovationtech.FoodMenuMasterApi.Entity.ItemMenuEntity;

@Repository
public interface ItemMenuRepository extends JpaRepository<ItemMenuEntity, Long> {
    
    // Buscar apenas itens ativos
    List<ItemMenuEntity> findByAtivoTrue();
    
    // Buscar itens por categoria
    List<ItemMenuEntity> findByCategoriaAndAtivoTrue(CategoryItem categoria);
    
    // Buscar por nome (case insensitive)
    List<ItemMenuEntity> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    
    // Buscar itens por faixa de preço
    List<ItemMenuEntity> findByPrecoBetweenAndAtivoTrue(BigDecimal precoMin, BigDecimal precoMax);
    
    // Buscar itens com avaliação mínima
    List<ItemMenuEntity> findByAvaliacaoGreaterThanEqualAndAtivoTrue(BigDecimal avaliacaoMinima);
    
    // Buscar itens com paginação
    Page<ItemMenuEntity> findByAtivo(Boolean ativo, Pageable pageable);
    
    // Buscar por categoria com paginação
    Page<ItemMenuEntity> findByCategoriaAndAtivo(CategoryItem categoria, Boolean ativo, Pageable pageable);
    
    // Query customizada para busca avançada
    @Query("SELECT i FROM ItemMenuEntity i WHERE " +
           "(:nome IS NULL OR LOWER(i.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:categoria IS NULL OR i.categoria = :categoria) AND " +
           "(:precoMin IS NULL OR i.preco >= :precoMin) AND " +
           "(:precoMax IS NULL OR i.preco <= :precoMax) AND " +
           "(:ativo IS NULL OR i.ativo = :ativo)")
    Page<ItemMenuEntity> buscarComFiltros(
        @Param("nome") String nome,
        @Param("categoria") CategoryItem categoria,
        @Param("precoMin") BigDecimal precoMin,
        @Param("precoMax") BigDecimal precoMax,
        @Param("ativo") Boolean ativo,
        Pageable pageable
    );
    
    // Verificar se existe item com o mesmo nome (útil para validações)
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
    boolean existsByNomeIgnoreCase(String nome);
    
    // Buscar item por QR Code
    Optional<ItemMenuEntity> findByQrCode(String qrCode);
    
    // Contar itens por categoria
    @Query("SELECT i.categoria, COUNT(i) FROM ItemMenuEntity i WHERE i.ativo = true GROUP BY i.categoria")
    List<Object[]> countItensByCategory();
}