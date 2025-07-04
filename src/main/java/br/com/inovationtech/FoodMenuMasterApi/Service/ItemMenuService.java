package br.com.inovationtech.FoodMenuMasterApi.Service;

import java.math.BigDecimal;
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
public class ItemMenuService {
    private final ItemMenuRepository repository;
    private final ItemMenuMapper mapper;

    /**
     * Busca todos os itens com paginação
     */
    @Transactional(readOnly = true)
    public Page<ItemMenuDTO> buscarTodos(Pageable pageable) {
        log.debug("Buscando todos os itens do cardápio com paginação: {}", pageable);
        
        Page<ItemMenuEntity> itens = repository.findAll(pageable);
        return itens.map(mapper::toDTO);
    }
    
    /**
     * Busca apenas itens ativos com paginação
     */
    @Transactional(readOnly = true)
    public Page<ItemMenuDTO> buscarAtivos(Pageable pageable) {
        log.debug("Buscando itens ativos do cardápio com paginação: {}", pageable);
        
        Page<ItemMenuEntity> itens = repository.findByActive(true, pageable);
        return itens.map(mapper::toDTO);
    }
    
    /**
     * Busca item por ID
     */
    @Transactional(readOnly = true)
    public ItemMenuDTO buscarPorId(Long id) {
        log.debug("Buscando item do cardápio por ID: {}", id);
        
        ItemMenuEntity item = repository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com ID: " + id));
        
        return mapper.toDTO(item);
    }
    
    /**
     * Busca com filtros avançados
     */
    @Transactional(readOnly = true)
    public Page<ItemMenuDTO> SearchingWithFilters(String name, CategoryItem category, BigDecimal minPrice, BigDecimal maxPrice, Boolean active, Pageable pageable) {
        log.debug("Buscando com filtros - name: {}, category: {}, price: {}-{}, active: {}", 
              name, category, minPrice, maxPrice, active);
        
        Page<ItemMenuEntity> items = repository.searchWithFilters(name, category, minPrice, maxPrice, active, pageable);
        return items.map(mapper::toDTO);
    }
    
    /**
     * Cria novo item
     */
    @Transactional
    public ItemMenuDTO createItem(ItemMenuDTO dto) {
        log.debug("Criando novo item do cardápio: {}", dto.getCategoryName());
        
        validationUniqueName(dto.getCategoryName(), null);
        
        ItemMenuEntity item = mapper.toEntity(dto);
        
        // Gerar QR Code único se não fornecido
        if (item.getQrCode() == null || item.getQrCode().trim().isEmpty()) {
            item.setQrCode(generateUniqueQrCode());
        }
        
        ItemMenuEntity itemSalvo = repository.save(item);
        log.info("Item criado com sucesso. ID: {}, Nome: {}", itemSalvo.getId(), itemSalvo.getName());
        
        return mapper.toDTO(itemSalvo);
    }
    
    /**
     * Atualiza item existente
     */
    @Transactional
    public ItemMenuDTO updateItemMenu(Long id, ItemMenuDTO dto) {
        log.debug("Atualizando item do cardápio. ID: {}, Nome: {}", id, dto.getName());
        
        ItemMenuEntity itemNonExistent = repository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com ID: " + id));
        
        validationUniqueName(dto.getName(), id);
        
        mapper.updateEntityFromDTO(dto, itemNonExistent);
        
        ItemMenuEntity itemUpdated = repository.save(itemNonExistent);
        log.info("Item atualizado com sucesso. ID: {}, Nome: {}", itemUpdated.getId(), itemUpdated.getName());
        
        return mapper.toDTO(itemUpdated);
    }
    
    /**
     * Remove item (exclusão lógica - marca como inativo)
     */
    @Transactional
    public void ItemDesactive(Long id) {
        log.debug("Desativando item do cardápio. ID: {}", id);
        
        ItemMenuEntity item = repository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com ID: " + id));
        
        item.setActive(false);
        repository.save(item);
        
        log.info("Item desativado com sucesso. ID: {}, Nome: {}", item.getId(), item.getName());
    }
    
    /**
     * Reativa item
     */
    @Transactional
    public void ItemActive(Long id) {
        log.debug("Ativando item do cardápio. ID: {}", id);
        
        ItemMenuEntity item = repository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com ID: " + id));
        
        item.setActive(true);
        repository.save(item);
        
        log.info("Item ativado com sucesso. ID: {}, Nome: {}", item.getId(), item.getName());
    }
    
    /**
     * Exclusão física (usar com cuidado)
     */
    @Transactional
    public void ItemDelete(Long id) {
        log.debug("Excluindo permanentemente item do cardápio. ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Item não encontrado com ID: " + id);
        }
        
        repository.deleteById(id);
        log.warn("Item excluído permanentemente. ID: {}", id);
    }
    
    /**
     * Busca por categoria
     */
    @Transactional(readOnly = true)
    public Page<ItemMenuDTO> getByCategory(CategoryItem category, Boolean active, Pageable pageable) {
        log.debug("Buscando itens por categoria: {}, active: {}", category, active);
        
        Page<ItemMenuEntity> items = repository.findByCategoryAndActive(category, active, pageable);
        return items.map(mapper::toDTO);
    }
    
    /**
     * Busca item por QR Code
     */
    @Transactional(readOnly = true)
    public ItemMenuDTO getByQrCode(String qrCode) {
        log.debug("Buscando item por QR Code: {}", qrCode);
        
        ItemMenuEntity item = repository.findByQrCode(qrCode)
          .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com QR Code: " + qrCode));
        
        return mapper.toDTO(item);
    }
    
    /**
     * Estatísticas dos itens por categoria
     */
    @Transactional(readOnly = true)
    public List<Object[]> getStatisticByCategory() {
        log.debug("Obtendo estatísticas de itens por categoria");
        return repository.countItemsByCategory();
    }
    
    // Métodos auxiliares privados
    
    /**
     * Valida se o nome do item é único
     */
    private void validationUniqueName(String name, Long idDelete) {
        boolean hasName = idDelete == null 
            ? repository.existsByNameIgnoreCase(name)
            : repository.existsByNameIgnoreCaseAndIdNot(name, idDelete);
            
        if (hasName) {
            throw new BusinessException("Já existe um item com o nome: " + name);
        }
    }
    
    /**
     * Gera um código QR único para o item
     */
    private String generateUniqueQrCode() {
        String qrCode;
        do {
            qrCode = "QR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (repository.findByQrCode(qrCode).isPresent());
        
        return qrCode;
    }
}
