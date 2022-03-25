package com.xielbs.config;

public class ClusterParameter {

	/**
	 * 集群名称
	 */
	public final static String DATABASE_SYSTEM = "DatabaseSystem";
	public final static String CLIENT_SYSTEM = "ClientSystem";
	public final static String COLLECTION_SYSTEM = "CollectionSystem";
	/**
	 * 集群主节点，可连接此地址加入集群
	 */
	public final static String DATABASE_SYSTEM_LEADER = "DatabaseSystemLeader";
	public final static String CLIENT_SYSTEM_LEADER = "ClientSystemLeader";
	public final static String COLLECTION_SYSTEM_LEADER = "CollectionSystemLeader";


	public final static String RPC_SERVICE = "RpcService";



	/**************************************************数据库操作服务配置********************************************************/
	/**
	 * 数据库操作服务监听Actor
	 */
	public final static String DATABASE_OPERATION_CLUSTER_LISTENER = "DbOperationClusterListener";
	/**
	 * 数据库操作路由
	 */
	public final static String DATABASE_OPERATION_ROUTER= "DbOperationRouter";
	/**
	 * 数据库操作Actor
	 */
	public final static String DATABASE_OPERATION_ACTOR = "DbOperationActor";


	/**************************************************路由服务配置********************************************************/
	/**
	 * 路由服务监听Actor
	 */
	public final static String ROUTER_CLUSTER_LISTENER = "RouterClusterListener";
	/**
	 * 路由服务角色名称
	 */
	public final static String ROUTER_ROLE = "Router";
	/**
	 * 内存库中路由服务地址
	 */
	public final static String ROUTER_IP_PORT = "routeIpPort";
	/**
	 * 路由服务Actor
	 */
	public final static String ROUTER_ACTOR = "routerActor";


	/**************************************************前置机服务配置********************************************************/
	/**
	 * 前置机服务监听Actor
	 */
	public final static String FRONT_MACHINE_CLUSTER_LISTENER = "FrontMachineClusterListener";
	/**
	 * 前置机角色名称
	 */
	public final static String FRONT_MACHINE_ROLE = "FrontMachine";
	/**
	 * 前置机集合
	 */
	public final static String FRONT_MACHINE_MAP = "frontMachineMap";
	/**
	 * 透传终端前置机接收信息服务actor名字
	 */
	public final static String FRONTMACHINE_RECEIVE_TRANSPOND_FROM_CLIENT = "frontmachineReceiveTranspondFromClient";
	/**
	 * 透传前置机接收终端返回信息服务actor名字
	 */
	public final static String FRONTMACHINE_RECEIVE_TRANSPOND_FROM_TRM = "frontmachineReceiveTranspondFromTrm";
	/**
	 * 前置机接收定时任务服务actor名字
	 */
	public final static String FRONTMACHINE_RECEIVE_ACTOR_PATH = "ReceiveFromTimeTask";

	/**
	 * 前置机接收主站召测服务actor名字
	 */
	public final static String FRONTMACHINE_RECEIVE_CALL_ACTOR_PATH = "ReceiveFromMasterStationCall";

	public final static String FRONT_MACHINE_RECEIVE_DISPATCH_TRM_MSG = "DispatchTrmMsg";

	public final static String FRONT_MACHINE_RECEIVE_TRM_LOG_REPORT = "TrmLogReport";


	/**************************************************定时任务服务配置********************************************************/

	/**
	 * 定时任务服务角色名称
	 */
	public final static String TIME_TASK_ROLE = "TimeTask";
	/**
	 * 定时任务集合
	 */
	public final static String TIME_TASK_MAP = "timeTaskMap";



	/**************************************************召测务服务配置********************************************************/

	/**
	 * 召测服务角色名称
	 */
	public final static String TRM_CALL_ROLE = "TrmCallSetService";


	/**************************************************其他配置********************************************************/
	/**
	 * 编码组
	 */
	public static final String DOC_DTI_GROUP = "doc_dti_group";
	/**
	 * 任务模板
	 */
	public static final String DOC_TASK_TMP = "doc_task_tmp";
	/**
	 * 遥信点信息
	 */
	public final static String  MP_TELE_INFO ="mp_tele_info";
	/**
	 * 遥信状态项
	 */
	public static final String YX_DICT_ITEM = "yx_dict_item";
	/**
	 * 发电机对应表计
	 */
	public final static String  MP_GENMP_INFO ="mp_gen_mp_info";
	public final static String  CP_PROTOCOL_ADDR ="cp_protocol_addr";
	/**
	 * 内存库中通道档案
	 */
	public final static String cp_info = "cp_info_";






	/**
	 * 定时任务接收前置机服务actor名字
	 */
	public final static String TIMETASK_RECEIVER_ACTOR_PATH = "TrmMsgReceiver";













	/**
	 * 透传终端列表
	 */
	public final static String TRANSPOND_TRM_LS = "transpondTrmLs";

	/**
	 * 透传终服务器IP端口
	 */
	public final static String TRANSPOND_SERVICE_IP = "transpondServiceIp";



	/**
	 * 定时任务下终端集合，名称前缀，timeTask_trm_10.13.175.36:2558
	 */
	public final static String TIMETASK_TRM_TABLE = "timetask_trm_";
	/**
	 * 终端所属定时任务
	 */
	public final static String TRM_TIMETASK_TABLE = "trm_timetask";

	/**
     * 记录所有登录过的终端;跟启动前置机无关
     */
	public final static String TRM_ALL_LOGINED =  "trm_all_logined";

	/**
	 * 目前在线的终端
	 * 前置机关闭后,会全部删除(由路由器删除),前置机启动后也删除
	 */
	public final static String TRM_LOG_IN = "trm_log_in";

	/**
	 * 终端所属前置机
	 * 本前置机启动后,登录过的终端,包括上线后又掉线的.但前置机关闭后,会全部删除(由路由器删除),前置机启动后也删除
	 */
	public final static String TRM_FRONTMACHINE_TABLE = "trm_frontmachine";







	/**
	 * timetask_frontMachineNum_10.13.175.36:2558
	 * 定时任务分配到的各前置机终端集合
	 */
	public final static String TIME_TASK_FM_NUM = "timetask_frontMachineNum_";



	/**
	 * 内存库中终端在线情况
	 */
	public final static String TRM_ONLINE = "trm_online";

	/**
	 * 终端上线时间列表
	 */
	public final static String  TRM_ONLINE_LS="trm_online_ls";

	public final static String  UNKNOWN_TRM="unknown_trm";
	/**
	 * 终端掉线时间列表
	 */
	public final static String  TRM_OFF_LS="trm_off_ls";



	/**
	 * 内存库中日志服务器IP端口
	 */
	public final static String LOG_SERVER_IPPORT = "LOG_SERVER_IPPORT";


	/**
	 * 内存库中日志服务器IP端口
	 */
	public final static String LOG_SERVER_URL = "LOG_SERVER";



	//通讯服务接受前置机消息ACTOR
	public final static String TRMCALLSET_SERVICE_RECE_FRONTMACHINE = "TrmCallSetServiceReceFT";
	//通讯服务接受主站消息ACTOR
	public final static String TRMCALLSET_SERVICE_RECE_MASTERSTATION = "TrmCallSetServiceReceMT";
	
	public final static String PUBLISHER_CONTENT_SUFF = "RMP_";
	
	
	public final static String  CP_DOC_CHANGE = "cp_doc_change";
	
	public final static String  MP_METER_INFO ="mp_meter_info";

	
	public final static String  ALARM_THRESHOLD ="alerm_threshold";
	
	public final static String  ALARM_RELA_OBJECT ="alerm_rela_object";
	

	
	public final static String  EVENT_LAST_RECORD ="event_last_record";	

	public final static String  YX_LAST_DATA  ="yx_last_calcu_data";	
	
	public final static String  YC_LAST_DATA  ="yc_last_calcu_data";	
	
	public final static String  MONITOR_LAST_XSL ="Monitor_last_xsl";	
	public final static String  MONITOR_LAST_WATERLEVEL  ="Monitor_last_waterlevel";	
	public final static String  MONITOR_LAST_WK  ="Monitor_last_wk";	
	
	public final static String  Generator_Mp_Last_Xsl="generator_mp_last_xsl";
	

	public final static String  MP_DL_CURVE  ="mp_dl_curve";	
	public final static String  MP_DL_DAY  ="mp_dl_day";	
	public final static String  MP_DL_MON  ="mp_dl_mon";	

	public final static String  OBJECT_DL_DAY  ="obj_dl_day";	
	public final static String  OBJECT_DL_MON  ="obj_dl_mon";	
	
	public final static String  THEORY_LOSS  ="theory_loss";	
	public final static String  COS_CONTRAST_PROJECT  ="cos_contrast_project";	
	public final static String  DEVICE_SPEC  ="device_spec";
	public final static String  BYQ_INFO  ="byq_info";
	
	
}
