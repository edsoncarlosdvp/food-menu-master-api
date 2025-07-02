package br.com.inovationtech.FoodMenuMasterApi.DTOs;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.inovationtech.FoodMenuMasterApi.Entity.ItemMenuEntity;

@Component
public class ItemMenuMapper {

  /**
     * Converte Entity para DTOs
     */
    public ItemMenuDTO toDTO(ItemMenuEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ItemMenuDTO dto = new ItemMenuDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setRating(entity.getRating());
        dto.setActive(entity.getActive());
        dto.setQrCode(entity.getQrCode());
        dto.setCreationDate(entity.getCreationDate());
        dto.setUpdateDate(entity.getUpdateDate());
        
        return dto;
    }
    
    /**
     * Converte DTO para Entity
     */
    public ItemMenuEntity toEntity(ItemMenuDTO dto) {
        if (dto == null) {
            return null;
        }
        
        ItemMenuEntity entity = new ItemMenuEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setRating(dto.getRating());
        entity.setActive(dto.getActive());
        entity.setQrCode(dto.getQrCode());
        
        return entity;
    }
    
    /**
     * Atualiza uma Entity existente com dados do DTO
     */
    public void updateEntityFromDTO(ItemMenuDTO dto, ItemMenuEntity entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setRating(dto.getRating());
        entity.setActive(dto.getActive());
        entity.setQrCode(dto.getQrCode());
    }
    
    /**
     * Converte lista de Entities para lista de DTOs
     */
    public List<ItemMenuDTO> toDTOList(List<ItemMenuEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Converte lista de DTOs para lista de Entities
     */
    public List<ItemMenuEntity> toEntityList(List<ItemMenuDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
