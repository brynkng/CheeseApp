package com.cheeseapp.Navigation;

/**
 * User: Bryan King
 * Date: 7/10/12
 */
public class TabInfo {

    private String _title;
    private Class _targetClass;

    public TabInfo (String title, Class targetClass) {
        _title = title;
        _targetClass = targetClass;
    }


    public String getTitle() {
        return _title;
    }

    public Class getTargetClass() {
        return _targetClass;
    }
}
