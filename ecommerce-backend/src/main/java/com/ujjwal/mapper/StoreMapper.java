package com.ujjwal.mapper;

import com.ujjwal.modal.Store;
import com.ujjwal.modal.User;
import com.ujjwal.payload.dto.StoreDTO;

public class StoreMapper {

    public static StoreDTO toDto(Store store) {
        if (store == null) {
            return null;
        }
        return StoreDTO.builder()
                .id(store.getId())
                .brand(store.getBrand())
                .storeAdminId(store.getStoreAdmin() != null ? store.getStoreAdmin().getId() : null)
                .storeAdmin(UserMapper.toDTO(store.getStoreAdmin()))
                .storeType(store.getStoreType())
                .description(store.getDescription())
                .contact(store.getContact())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .status(store.getStatus())
                .build();
    }

    public static Store toEntity(StoreDTO dto, User storeAdmin) {
        Store.StoreBuilder builder = Store.builder()
                .id(dto.getId())
                .brand(dto.getBrand())
                .storeAdmin(storeAdmin)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .storeType(dto.getStoreType())
                .description(dto.getDescription());

        // Set contact info if provided
        if (dto.getContact() != null) {
            builder.contact(dto.getContact());
        }

        return builder.build();
    }
}
