package my.carfix.carfix.model;

public class DrawerItem
{
    private int resourceId;

    private String name;

    public DrawerItem(int resourceId, String name)
    {
        setResourceId(resourceId);
        setName(name);
    }

    public int getResourceId()
    {
        return resourceId;
    }

    public void setResourceId(int resourceId)
    {
        this.resourceId = resourceId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
