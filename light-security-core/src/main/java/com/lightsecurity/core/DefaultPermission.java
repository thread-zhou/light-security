package com.lightsecurity.core;

public final class DefaultPermission extends AbstractPermission{

    private final String permissionMethod;
    private final String permissionDesc;

    public DefaultPermission(String name, String pattern, boolean enabled, String method, String desc){
        super(name, pattern, enabled);
        this.permissionMethod = method;
        this.permissionDesc = desc;
    }


    /**
     * 只会初始化父类中的属性，子类的为null
     * @param name
     * @param pattern
     * @param enabled
     */
    public DefaultPermission(String name, String pattern, boolean enabled) {
        this(name, pattern, enabled, null, null);
    }

    public String getPermissionMethod() {
        return permissionMethod;
    }

    public String getPermissionDesc() {
        return permissionDesc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(":");
        sb.append("permissionName:").append(this.permissionName).append(";");
        sb.append("permissionPattern:").append(this.getPermissionPattern()).append(";");
        sb.append("permissionMethod:").append(this.permissionMethod).append(";");
        sb.append("permissionDesc:").append(this.permissionDesc).append(";");
        sb.append("permissionEnabled:").append(this.enabled).append(";");
        return sb.toString();
    }
}
