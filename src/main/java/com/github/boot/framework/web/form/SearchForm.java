package com.github.boot.framework.web.form;

/**
 * 搜索表单
 * Created by cjh on 2017/3/24.
 */
public class SearchForm implements Form{

    private String searchText;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
