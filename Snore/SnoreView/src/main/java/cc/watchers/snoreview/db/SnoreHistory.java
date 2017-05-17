package cc.watchers.snoreview.db;

/**
 * Created by jc on 2017/5/12.
 */

public class SnoreHistory {
    String id;
    String rowFile;
    String logFile;
    String createTime;
    String lastUpdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRowFile() {
        return rowFile;
    }

    public void setRowFile(String rowFile) {
        this.rowFile = rowFile;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String last_update) {
        this.lastUpdate = last_update;
    }

    @Override
    public String toString(){
        return "id:" + id +" raw:"+rowFile+" log:"+logFile+" create:"+createTime+" update"+lastUpdate;
    }
}
