package neoAtlantis.utilidades.apps.catalogs.interfaces;

import java.util.List;
import java.util.Map;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryCatalogs;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryTable;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public interface CatalogsLoader {
    public static final String SEPARADOR_LLAVE=" ";

    public MemoryCatalogs loadCatalogs(MemoryCatalogs mc) throws Exception;
    public List<Object[]> getData(MemoryTable t) throws Exception;
    public long getRecordsCount(MemoryTable t) throws Exception;
    public int updateData(MemoryTable t, Map<String,Object> data) throws Exception;
    public int addData(MemoryTable t, Map<String,Object> data) throws Exception;
    public Map<String,Object> getDataById(MemoryTable t, String key) throws Exception;
}
