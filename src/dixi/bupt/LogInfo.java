package dixi.bupt;

public class LogInfo {
    private String id="";//id
    private String module="";//模块
    private String level="";//级别
    private String log="";
    private String logtext="";
    public LogInfo(){
        this.id="";
        this.module="";
        this.level="";
    }

    /**
     * 解析一个logtext
     * @param logtext
     */
    public void analysisLog(String logtext){
        //[20200420-200901-998][Shop][INFO]+call buy service, orderId=1
        //获取id
        this.id = logtext.substring(1,20);
        int module_start_index = logtext.indexOf(']')+1;
        int module_end_index = logtext.indexOf(']', module_start_index);
        this.module = logtext.substring(module_start_index,module_end_index);//[start,end)
        int level_start_index = module_end_index+2;
        int level_end_index = logtext.indexOf(']', level_start_index);
        this.level = logtext.substring(level_start_index, level_end_index);

        int logIndex = logtext.indexOf('+')+1;
        this.log = logtext.substring(logIndex);
        this.logtext = logtext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return this.logtext;
    }
}
