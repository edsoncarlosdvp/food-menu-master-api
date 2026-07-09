package br.com.inovationtech.FoodMenuMasterApi.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.inovationtech.FoodMenuMasterApi.DTOs.ItemMenuDTO;
import br.com.inovationtech.FoodMenuMasterApi.DTOs.ItemMenuMapper;
import br.com.inovationtech.FoodMenuMasterApi.Entity.CategoryItem;
import br.com.inovationtech.FoodMenuMasterApi.Entity.ItemMenuEntity;
import br.com.inovationtech.FoodMenuMasterApi.Exceptions.BusinessException;
import br.com.inovationtech.FoodMenuMasterApi.Exceptions.ResourceNotFoundException;
import br.com.inovationtech.FoodMenuMasterApi.Repository.ItemMenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemMenuService {
    
    private final ItemMenuRepository itemMenuRepository;
    private final ItemMenuMapper itemMenuMapper;
    
    /**
     * Lista todos os itens com paginação
     */
    @Transactional(readOnly = true)
    public Page<ItemMenuDTO> listAllMenuItensWithPagination(Pageable pageable) {
        log.info("Buscando todos os itens do cardápio");
        
        Page<ItemMenuEntity> entities = itemMenuRepository.findAll(pageable);
        return entities.map(itemMenuMapper::toDTO);
    }
    
    /**
     * Lista apenas itens ativos com paginação
     */
    @Transactional(readOnly = true)
    public Page<ItemMenuDTO> listOnlyActiveItens(Pageable pageable) {
        log.info("Buscando itens ativos do cardápio");
        
        Page<ItemMenuEntity> entities = itemMenuRepository.findByActiveTrue(pageable);
        return entities.map(itemMenuMapper::toDTO);
    }
    
    /**
     * Busca item por ID
     */
    @Transactional(readOnly = true)
    public ItemMenuDTO searchItensById(Long id) {
        log.info("Buscando item por ID: {}", id);
        
        if (id == null) {
            throw new BusinessException("ID não pode ser nulo");
        }
        
        ItemMenuEntity entity = itemMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com ID: " + id));
        
        log.info("Item encontrado: {}", entity.getName());
        return itemMenuMapper.toDTO(entity);
    }
    
    /**
     * Busca item por QR Code
     */
    @Transactional(readOnly = true)
    public ItemMenuDTO searchByQrCode(String qrCode) {
        log.info("Buscando item por QR Code: {}", qrCode);
        
        if (qrCode == null || qrCode.trim().isEmpty()) {
            throw new BusinessException("QR Code não pode ser nulo ou vazio");
        }
        
        ItemMenuEntity entity = itemMenuRepository.findByQrCodeAndActiveTrue(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com QR Code: " + qrCode));
        
        log.info("Item encontrado por QR Code: {}", entity.getName());
        return itemMenuMapper.toDTO(entity);
    }
    
    /**
     * Busca com filtros avançados (nome, categoria, faixa de preço e status)
     */
    @Transactional(readOnly = true)
    public Page<ItemMenuDTO> searchWithFilters(String name, CategoryItem category,
            BigDecimal minPrice, BigDecimal maxPrice, Boolean active, Pageable pageable) {

        log.info("Buscando itens com filtros - Nome: {}, Categoria: {}, Preco: [{}, {}], Ativo: {}",
                name, category, minPrice, maxPrice, active);

        Page<ItemMenuEntity> entities = itemMenuRepository.searchWithFilters(
                name, category, minPrice, maxPrice, active, pageable);

        return entities.map(itemMenuMapper::toDTO);
    }
    
    /**
     * Busca por categoria
     */
    @Transactional(readOnly = true)
    public Page<ItemMenuDTO> searchByCategory(CategoryItem category, Boolean active, Pageable pageable) {
        log.info("Buscando itens por categoria: {}, Ativo: {}", category, active);
        
        if (category == null) {
            throw new BusinessException("Categoria não pode ser nula");
        }
        
        Page<ItemMenuEntity> entities;
        
        if (active == null || active) {
            entities = itemMenuRepository.findByCategoryAndActiveTrue(category, pageable);
        } else {
            entities = itemMenuRepository.findByCategory(category, pageable);
        }
        
        return entities.map(itemMenuMapper::toDTO);
    }
    
    /**
     * Cria novo item
     */
    public ItemMenuDTO createMenuItem(ItemMenuDTO dto) {
        log.info("Criando novo item: {}", dto.getName());
        
        // Validações
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BusinessException("Nome do item é obrigatório");
        }
        
        // Verificar se já existe item com mesmo nome
        if (itemMenuRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BusinessException("Já existe um item com o nome: " + dto.getName());
        }
        
        ItemMenuEntity entity = itemMenuMapper.toEntity(dto);
        // O ID é sempre gerado pelo banco; ignoramos qualquer valor vindo do client
        // para evitar que a criação sobrescreva um registro existente.
        entity.setId(null);
        entity.setActive(true);
        entity.setCreationDate(LocalDateTime.now());
        entity.setUpdateDate(LocalDateTime.now());
        
        // Gerar QR Code único
        entity.setQrCode(generateUniqueQrCode());
        
        ItemMenuEntity savedEntity = itemMenuRepository.save(entity);
        
        log.info("Item criado com sucesso: {}", savedEntity.getName());
        return itemMenuMapper.toDTO(savedEntity);
    }
    
    /**
     * Atualiza item existente
     */
    public ItemMenuDTO updateMenuItem(Long id, ItemMenuDTO dto) {
        log.info("Atualizando item ID: {}", id);
        
        ItemMenuEntity entity = itemMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com ID: " + id));
        
        // Verificar se nome já existe em outro item
        if (itemMenuRepository.existsByNameIgnoreCaseAndIdNot(dto.getName(), id)) {
            throw new BusinessException("Já existe outro item com o nome: " + dto.getName());
        }
        
        itemMenuMapper.updateEntityFromDTO(dto, entity);
        entity.setUpdateDate(LocalDateTime.now());
        
        ItemMenuEntity updatedEntity = itemMenuRepository.save(entity);
        
        log.info("Item atualizado com sucesso: {}", updatedEntity.getName());
        return itemMenuMapper.toDTO(updatedEntity);
    }
    
    /**
     * Desativa item (exclusão lógica)
     */
    public void ItemDesactive(Long id) {
        log.info("Desativando item ID: {}", id);
        
        ItemMenuEntity entity = itemMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com ID: " + id));
        
        entity.setActive(false);
        entity.setUpdateDate(LocalDateTime.now());
        
        itemMenuRepository.save(entity);
        
        log.info("Item desativado com sucesso: {}", entity.getName());
    }
    
    /**
     * Ativa item
     */
    public void ItemActive(Long id) {
        log.info("Ativando item ID: {}", id);
        
        ItemMenuEntity entity = itemMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com ID: " + id));
        
        entity.setActive(true);
        entity.setUpdateDate(LocalDateTime.now());
        
        itemMenuRepository.save(entity);
        
        log.info("Item ativado com sucesso: {}", entity.getName());
    }
    
    /**
     * Exclui item permanentemente
     */
    public void ItemDelete(Long id) {
        log.info("Excluindo permanentemente item ID: {}", id);
        
        if (!itemMenuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item não encontrado com ID: " + id);
        }
        
        itemMenuRepository.deleteById(id);
        
        log.info("Item excluído permanentemente com ID: {}", id);
    }
    
    /**
     * Obtém estatísticas por categoria
     */
    @Transactional(readOnly = true)
    public List<Object[]> getStatisticByCategory() {
        log.info("Obtendo estatísticas por categoria");
        
        List<Object[]> statistics = itemMenuRepository.countItemsByCategory();
        
        log.info("Estatísticas obtidas para {} categorias", statistics.size());
        return statistics;
    }
    
    /**
     * Busca item por ID dentro de uma categoria específica
     */
    @Transactional(readOnly = true)
    public ItemMenuDTO searchByIdInCategory(Long id, CategoryItem category) {
        log.info("Buscando item ID: {} na categoria: {}", id, category);
        
        if (id == null) {
            throw new BusinessException("ID não pode ser nulo");
        }
        
        if (category == null) {
            throw new BusinessException("Categoria não pode ser nula");
        }
        
        ItemMenuEntity entity = itemMenuRepository.findByIdAndCategoryAndActiveTrue(id, category)
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Item com ID %d não encontrado na categoria %s", id, category)
                ));
        
        log.info("Item encontrado: {} na categoria: {}", entity.getName(), category);
        return itemMenuMapper.toDTO(entity);
    }
    
    /**
     * Gera QR Code único.
     * Usa UUID em vez de timestamp para eliminar risco de colisão sob concorrência.
     */
    private String generateUniqueQrCode() {
        String qrCode;
        do {
            qrCode = "QR_" + UUID.randomUUID();
        } while (itemMenuRepository.existsByQrCode(qrCode));
        
        return qrCode;
    }
}
