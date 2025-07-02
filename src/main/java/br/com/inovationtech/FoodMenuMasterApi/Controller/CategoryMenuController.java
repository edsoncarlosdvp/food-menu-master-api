package br.com.inovationtech.FoodMenuMasterApi.Controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.inovationtech.FoodMenuMasterApi.Entity.CategoryItem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/categorias")
@CrossOrigin(origins = "*")
public class CategoryMenuController {
    
    /**
     * Lista todas as categorias disponíveis
     * GET /api/categorias
     */
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> listarCategorias() {
        log.info("Requisição para listar todas as categorias");
        
        List<Map<String, String>> categories = Arrays.stream(CategoryItem.values())
                .map(item -> Map.of(
                    "codigo", item.name(),
                    "nome", item.getName(),
                    "descricao", item.getDescription()
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Busca categoria por código
     * GET /api/categorias/PIZZA
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<Map<String, String>> buscarCategoriaPorCodigo(@PathVariable String codigo) {
        log.info("Requisição para buscar categoria por código: {}", codigo);
        
        try {
            CategoryItem categoria = CategoryItem.valueOf(codigo.toUpperCase());
            
            Map<String, String> response = Map.of(
                "codigo", categoria.name(),
                "nome", categoria.getName(),
                "descricao", categoria.getDescription()
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
