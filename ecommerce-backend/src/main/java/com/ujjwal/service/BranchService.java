package com.ujjwal.service;

import com.ujjwal.exception.UserException;
import com.ujjwal.modal.User;
import com.ujjwal.payload.dto.BranchDTO;

import java.util.List;

public interface BranchService {
    BranchDTO createBranch(BranchDTO branchDto, User user);

    BranchDTO getBranchById(Long id);

    List<BranchDTO> getAllBranchesByStoreId(Long storeId) throws UserException;

    List<BranchDTO> getAllBranches();

    BranchDTO updateBranch(Long id, BranchDTO branchDto, User user) throws Exception;

    void deleteBranch(Long id);
}
