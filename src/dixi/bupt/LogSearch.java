package dixi.bupt;
import java.util.ArrayList;
import java.util.HashMap;

public class LogSearch {
    /*
    [20200420-200901-998][Shop][INFO]+call buy service, orderId=1
    [20200420-200901-220][Shop][ERROR]+call buy service exception[msg=db is connect timeout], orderId=1
    [20200420-200901-225][Shop][WARN]+execute order failure, orderId=1
    [20200420-200903-002][PRD][INFO]+add new product, title="short t-shirt"
    [20200420-200903-005][PRD][INFO]+modify product, id=5, title="fish"
    [20200420-200903-012][PRD][WARN]+delete product, id=2, modifier=xueshen
    */
    public ArrayList<LogInfo> infoArrayList = new ArrayList<>();

    public LogSearch() {

    }

    public void logFileInput(ArrayList<String> logs) {
        int index=0;
        ArrayList<Integer> moduleList;
        ArrayList<Integer> levelList;
        for (String log : logs) {
            LogInfo info = new LogInfo();
            info.analysisLog(log);
            infoArrayList.add(info);
        }
    }

    /**
     * 按照级别返回
     * @param level
     * @return
     */
    private ArrayList<LogInfo> searchByLevel(String level){
        ArrayList<LogInfo> arrayList = new ArrayList<>();
        for(LogInfo info : infoArrayList)
        {
            if(info.getLevel().equals(level))
            {
                arrayList.add(info);
            }
        }
        return arrayList;
    }

    /**
     * 按照模块返回
     * @param module
     * @return
     */
    private ArrayList<LogInfo> searchByModule(String module){
        ArrayList<LogInfo> arrayList = new ArrayList<>();
        for(LogInfo info : infoArrayList)
        {
            if(info.getModule().equals(module))
            {
                arrayList.add(info);
            }
        }
        return arrayList;
    }

    /**
     * 按模块和级别返回
     * @param module
     * @param level
     * @return
     */
    private ArrayList<LogInfo> searchByModuleAndLevel(String module, String level)
    {
        ArrayList<LogInfo> arrayList = new ArrayList<>();
        for(LogInfo info : infoArrayList)
        {
            if(info.getModule().equals(module) && info.getLevel().equals(level))
            {
                arrayList.add(info);
            }
        }
        return arrayList;
    }

    //
    public void ShowLog(String module, String level)
    {
        if(!"".equals(module) && !"".equals(level))
        {
            ArrayList<LogInfo> res = searchByModuleAndLevel(module,level);
            for (LogInfo info :
                    res) {
                System.out.println(res);
            }
        }
    }
}

