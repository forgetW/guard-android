package cn.withub.guard.data;

import java.io.Serializable;
import java.util.List;

public class Role implements Serializable {

    /**
     * roleName : 系统管理员
     * roleCode : system
     * id : f235c9a3a5d9494cacade6b52b75c323
     * userId : f89bc94986954d3db84c1c0e4ad49504
     * roleId : system
     * tenantId : nmg
     * createdBy :
     * createdAt : 2022-06-11T07:41:25.111Z
     * updatedBy : null
     * updatedAt : 2022-06-11T07:41:25.112Z
     * deletedBy : null
     * deletedAt : 0
     * lockedBy : null
     * lockedAt : null
     * ownerId : null
     * version : 0
     * sort :
     * remark :
     * startTime : 2022-06-08T08:14:18.000Z
     * endTime : null
     * level : 5
     * orgScope : null
     * mdApps : []
     */

    private String roleName;
    private String roleCode;
    private String id;
    private String userId;
    private String roleId;
    private String tenantId;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt;
    private String deletedBy;
    private int deletedAt;
    private String lockedBy;
    private String lockedAt;
    private String ownerId;
    private int version;
    private String sort;
    private String remark;
    private String startTime;
    private String endTime;
    private int level;
    private String orgScope;
    private List<?> mdApps;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public int getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(int deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Object getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public Object getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(String lockedAt) {
        this.lockedAt = lockedAt;
    }

    public Object getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Object getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Object getOrgScope() {
        return orgScope;
    }

    public void setOrgScope(String orgScope) {
        this.orgScope = orgScope;
    }

    public List<?> getMdApps() {
        return mdApps;
    }

    public void setMdApps(List<?> mdApps) {
        this.mdApps = mdApps;
    }
}
