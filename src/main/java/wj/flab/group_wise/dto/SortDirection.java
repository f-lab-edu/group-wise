package wj.flab.group_wise.dto;

public enum SortDirection {
    ASC("오름차순"),
    DESC("내림차순");

    private final String description;

    SortDirection(String description) {
        this.description = description;
    }

    public boolean isAsc() {
        return this == ASC;
    }
}
