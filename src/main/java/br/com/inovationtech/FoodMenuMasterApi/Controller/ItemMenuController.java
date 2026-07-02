package br.com.inovationtech.FoodMenuMasterApi.Controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.inovationtech.FoodMenuMasterApi.DTOs.ItemMenuDTO;
import br.com.inovationtech.FoodMenuMasterApi.Entity.CategoryItem;
import br.com.inovationtech.FoodMenuMasterApi.Service.ItemMenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/itens-cardapio")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para desenvolvimento - ajustar em produção
public class ItemMenuController {
    
    private final ItemMenuService service;
    
    /**
     * Lista todos os itens com paginação
     * GET /api/itens-cardapio
     */
    @GetMapping
    public ResponseEntity<Page<ItemMenuDTO>> getAllMenuItens(
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Requisição para listar todos os itens do cardápio");
        
        Page<ItemMenuDTO> itens = service.listAllMenuItensWithPagination(pageable);
        return ResponseEntity.ok(itens);
    }
    
    /**
     * Lista apenas itens ativos com paginação
     * GET /api/itens-cardapio/ativos
     */
    @GetMapping("/ativos")
    public ResponseEntity<Page<ItemMenuDTO>> getOnlyActiveItens(
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Requisição para listar itens ativos do cardápio");
        
        Page<ItemMenuDTO> itens = service.listOnlyActiveItens(pageable);
        return ResponseEntity.ok(itens);
    }
    
    /**
     * Busca item por ID
     * GET /api/itens-cardapio/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemMenuDTO> getItensById(@PathVariable Long id) {
        log.info("Requisição para buscar item por ID: {}", id);
        
        ItemMenuDTO item = service.searchItensById(id);
        return ResponseEntity.ok(item);
    }
    
    /**
     * Busca item por QR Code
     * GET /api/itens-cardapio/qr/QR-12345678
     */
    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<ItemMenuDTO> getByQrCode(@PathVariable String qrCode) {
        log.info("Requisição para buscar item por QR Code: {}", qrCode);
        
        ItemMenuDTO item = service.searchByQrCode(qrCode);
        return ResponseEntity.ok(item);
    }
    
    /**
     * Busca com filtros avançados
     * GET /api/itens-cardapio/buscar?nome=pizza&categoria=PIZZA&precoMin=10&precoMax=50&ativo=true
     */
    @GetMapping("/buscar")
    public ResponseEntity<Page<ItemMenuDTO>> getWithFilters(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) CategoryItem category,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.info("Requisição para buscar itens com filtros - Nome: {}, Categoria: {}", nome, category);
        
        Page<ItemMenuDTO> itens = service.SearchWithFilters(nome, category, precoMin, precoMax, ativo, pageable);
        return ResponseEntity.ok(itens);
    }
    
    /**
     * Busca por categoria
     * GET /api/itens-cardapio/categoria/PIZZA?ativo=true
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<Page<ItemMenuDTO>> getByCategory(
            @PathVariable CategoryItem category,
            @RequestParam(defaultValue = "true") Boolean active,
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.info("Requisição para buscar itens por categoria: {}", category);
        
        Page<ItemMenuDTO> itens = service.searchByCategory(category, active, pageable);
        return ResponseEntity.ok(itens);
    }
    
    /**
     * Cria novo item
     * POST /api/itens-cardapio
     */
    @PostMapping
    public ResponseEntity<ItemMenuDTO> createMenuItem(@Valid @RequestBody ItemMenuDTO dto) {
        log.info("Requisição para criar novo item: {}", dto.getName());
        
        ItemMenuDTO itemCriado = service.createMenuItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemCriado);
    }
    
    /**
     * Atualiza item existente
     * PUT /api/itens-cardapio/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemMenuDTO> updateMenuItem(
            @PathVariable Long id, 
            @Valid @RequestBody ItemMenuDTO dto) {
        log.info("Requisição para atualizar item ID: {}", id);
        
        ItemMenuDTO updatedItem = service.updateMenuItem(id, dto);
        return ResponseEntity.ok(updatedItem);
    }
    
    /**
     * Desativa item (exclusão lógica)
     * PATCH /api/itens-cardapio/1/desativar
     */
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> itemDesactive(@PathVariable Long id) {
        log.info("Requisição para desativar item ID: {}", id);
        
        service.ItemDesactive(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Ativa item
     * PATCH /api/itens-cardapio/1/ativar
     */
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> itemActive(@PathVariable Long id) {
        log.info("Requisição para ativar item ID: {}", id);
        
        service.ItemActive(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Exclui item permanentemente
     * DELETE /api/itens-cardapio/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> itemDelete(@PathVariable Long id) {
        log.info("Requisição para excluir permanentemente item ID: {}", id);
        
        service.ItemDelete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Obtém estatísticas por categoria
     * GET /api/itens-cardapio/estatisticas/categoria
     */
    @GetMapping("/estatisticas/categoria")
    public ResponseEntity<List<Object[]>> getStatisticByCategory() {
        log.info("Requisição para obter estatísticas por categoria");
        
        List<Object[]> estatisticas = service.getStatisticByCategory();
        return ResponseEntity.ok(estatisticas);
    }

    /**
     * Busca item específico por ID dentro de uma categoria
     * GET /itens-cardapio/categoria/PIZZA/item/1
     */
    @GetMapping("/categoria/{categoria}/item/{id}")
    public ResponseEntity<ItemMenuDTO> getByIdInCategory(
        @PathVariable Long id, 
        @PathVariable CategoryItem category) {
        
        log.info("Requisição para buscar item ID: {} na categoria: {}", id, category);
        
        ItemMenuDTO item = service.searchByIdInCategory(id, category);
        return ResponseEntity.ok(item);
    }
}
