package br.com.inovationtech.FoodMenuMasterApi.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import br.com.inovationtech.FoodMenuMasterApi.DTOs.ItemMenuDTO;
import br.com.inovationtech.FoodMenuMasterApi.DTOs.ItemMenuMapper;
import br.com.inovationtech.FoodMenuMasterApi.Entity.CategoryItem;
import br.com.inovationtech.FoodMenuMasterApi.Entity.ItemMenuEntity;
import br.com.inovationtech.FoodMenuMasterApi.Exceptions.BusinessException;
import br.com.inovationtech.FoodMenuMasterApi.Exceptions.ResourceNotFoundException;
import br.com.inovationtech.FoodMenuMasterApi.Repository.ItemMenuRepository;

public class ItemMenuService {
    private final ItemMenuRepository repository;
    private final ItemMenuMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(ItemMenuService.class);
    
    public ItemMenuService(ItemMenuRepository repository, ItemMenuMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
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
        
        Page<ItemMenuEntity> itens = repository.findByAtivo(true, pageable);
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
    public Page<ItemMenuDTO> buscarComFiltros(String nome, CategoryItem categoria, BigDecimal precoMin, BigDecimal precoMax, Boolean ativo, Pageable pageable) {
        log.debug("Buscando itens com filtros - Nome: {}, Categoria: {}, PreçoMin: {}, PreçoMax: {}, Ativo: {}", nome, categoria, precoMin, precoMax, ativo);
        
        Page<ItemMenuEntity> itens = repository.buscarComFiltros(nome, categoria, precoMin, precoMax, ativo, pageable);
        return itens.map(mapper::toDTO);
    }
    
    /**
     * Cria novo item
     */
    @Transactional
    public ItemMenuDTO criar(ItemMenuDTO dto) {
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
    public ItemMenuDTO atualizar(Long id, ItemMenuDTO dto) {
        log.debug("Atualizando item do cardápio. ID: {}, Nome: {}", id, dto.getName());
        
        ItemMenuEntity itemExistente = repository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com ID: " + id));
        
        validationUniqueName(dto.getName(), id);
        
        mapper.updateEntityFromDTO(dto, itemExistente);
        
        ItemMenuEntity itemAtualizado = repository.save(itemExistente);
        log.info("Item atualizado com sucesso. ID: {}, Nome: {}", itemAtualizado.getId(), itemAtualizado.getName());
        
        return mapper.toDTO(itemAtualizado);
    }
    
    /**
     * Remove item (exclusão lógica - marca como inativo)
     */
    @Transactional
    public void desactive(Long id) {
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
    public void ativar(Long id) {
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
    public void delete(Long id) {
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
    public Page<ItemMenuDTO> getByCategory(CategoryItem categoria, Boolean ativo, Pageable pageable) {
        log.debug("Buscando itens por categoria: {}, ativo: {}", categoria, ativo);
        
        Page<ItemMenuEntity> itens = repository.findByCategoriaAndAtivo(categoria, ativo, pageable);
        return itens.map(mapper::toDTO);
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
        return repository.countItensByCategory();
    }
    
    // Métodos auxiliares privados
    
    /**
     * Valida se o nome do item é único
     */
    private void validationUniqueName(String name, Long idDelete) {
        boolean hasName = idDelete == null 
            ? repository.existsByNomeIgnoreCase(name)
            : repository.existsByNomeIgnoreCaseAndIdNot(name, idDelete);
            
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
