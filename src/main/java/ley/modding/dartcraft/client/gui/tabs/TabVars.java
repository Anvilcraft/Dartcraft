package ley.modding.dartcraft.client.gui.tabs;

public class TabVars {
    private static Class openedLeftTab;

    private static Class openedRightTab;

    public static Class getOpenedLeftTab() {
        return openedLeftTab;
    }

    public static Class getOpenedRightTab() {
        return openedRightTab;
    }

    public static void setOpenedLeftTab(Class tabClass) {
        openedLeftTab = tabClass;
    }

    public static void setOpenedRightTab(Class tabClass) {
        openedRightTab = tabClass;
    }
}
