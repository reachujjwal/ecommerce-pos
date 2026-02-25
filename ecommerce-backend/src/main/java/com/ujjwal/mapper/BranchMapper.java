package com.ujjwal.mapper;

import com.ujjwal.modal.Branch;
import com.ujjwal.modal.Store;
import com.ujjwal.payload.dto.BranchDTO;

public class BranchMapper {

    public static BranchDTO toDto(Branch branch) {
        BranchDTO dto = new BranchDTO();
        dto.setId(branch.getId());
        dto.setName(branch.getName());
        dto.setAddress(branch.getAddress());
        dto.setPhone(branch.getPhone());
        dto.setEmail(branch.getEmail());
        dto.setCloseTime(branch.getCloseTime());
        dto.setOpenTime(branch.getOpenTime());
        dto.setWorkingDays(branch.getWorkingDays());
        dto.setStoreId(branch.getStore() != null ? branch.getStore().getId() : null);
        dto.setStore(StoreMapper.toDto(branch.getStore()));
        dto.setCreatedAt(branch.getCreatedAt());
        dto.setUpdatedAt(branch.getUpdatedAt());
        dto.setManager(branch.getManager() != null ? branch.getManager().getFullName() : null);
        return dto;
    }

    public static Branch toEntity(BranchDTO dto, Store store) {
        Branch branch = new Branch();
        branch.setId(dto.getId());
        branch.setName(dto.getName());
        branch.setAddress(dto.getAddress());
        branch.setStore(store);
        branch.setEmail(dto.getEmail());
        branch.setPhone(dto.getPhone());
        branch.setCloseTime(dto.getCloseTime());
        branch.setOpenTime(dto.getOpenTime());
        branch.setWorkingDays(dto.getWorkingDays());
        branch.setCreatedAt(dto.getCreatedAt());
        branch.setUpdatedAt(dto.getUpdatedAt());
        return branch;
    }
}
