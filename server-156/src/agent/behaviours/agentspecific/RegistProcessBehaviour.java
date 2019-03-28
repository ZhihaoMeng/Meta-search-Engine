package agent.behaviours.agentspecific;

import server.info.config.MyEnums.RegisterResult;
import server.info.entites.transactionlevel.UserEntity;
import server.info.entites.transactionlevel.UserInfoEntity;
import agent.data.inblackboard.RegistData;
import agent.data.inmsg.DataFromInterfaceAgent;
import agent.entities.blackboard.RegistDataBlackboard;
import common.textprocess.userXMLfilehelpler.UserXMLHelper;
import db.dbhelpler.CitiesHelper;
import db.dbhelpler.JobHelper;
import db.dbhelpler.ProvincesHelper;
import db.dbhelpler.UserHelper;
import db.dbhelpler.UserInfoHelper;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class RegistProcessBehaviour extends Behaviour{

	protected DataFromInterfaceAgent mData;
	protected String mUsername;
	protected String mPasswd;
	protected String mEmail;
	protected String mHome;
	protected String mCity;
	protected String mJob;
	protected String mCompany;
	protected String mCookieid;
	protected int mUserid;
	protected int mBlackboardIndex;
	
	@Override
	public void action() {
		
		ACLMessage msg=myAgent.receive();
		if(null!=msg){
			RegistData data = null;
			try {
				mData=null;
				Object obj=msg.getContentObject();
				if(!DataFromInterfaceAgent.class.isInstance(obj)) return;
				mData=(DataFromInterfaceAgent)obj;
				if(null==mData) return;
				mBlackboardIndex=mData.getIndex();
				data=(RegistData)RegistDataBlackboard.getData(mBlackboardIndex);
				mUsername=data.getUsername();
				mPasswd=data.getPassword();
				mEmail = data.getEmailadress();
				String mHomeId = data.getHome();
				String mCityId = data.getCity();
				String mJobId = data.getJob();
				mCompany = data.getCompany();
				boolean isExistu = UserHelper.isExistU(mUsername);
				boolean isExiste = UserHelper.isExistE(mEmail);
				
				//home city job 是id 换成内容后存入userinfo
				mHome = ProvincesHelper.getProvinceById(mHomeId);
				mCity = CitiesHelper.getCityById(mCityId);
				mJob = JobHelper.getJobById(mJobId);
				
				if(isExistu) data.setRegistState(RegisterResult.username_exit);
				else if(isExiste) data.setRegistState(RegisterResult.email_exist);
				else{
					UserEntity user = new UserEntity();
					user.setUsername(mUsername);
					user.setPassword(mPasswd);
					user.setEmail(mEmail);
					UserInfoEntity userInfo = new UserInfoEntity();
					int uid = UserHelper.addUserEntity(user);
					
					userInfo.setUid(uid);
					userInfo.setAddress(mCity);
					userInfo.setHome(mHome);
					userInfo.setJob(mJob);
					userInfo.setCompany(mCompany);
					int uiid = UserInfoHelper.addUserInfoEntity(userInfo);
//					String userId = String.valueOf(uid);
//					UserXMLHelper.getInstance().createUserXMLFile(userId);// 生成本次注册用户的兴趣XML文件（by许静
																			// 20121105)
					if(UserHelper.isLegalUserID(uid)&& UserInfoHelper.isLegalUserInfoID(uiid)) data.setRegistState(RegisterResult.success);
					else data.setRegistState(RegisterResult.illegal_info);//这里认为没有把信息放入数据库是因为注册信息有错
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(null!=data){
					data.done();
				}
			}
		}else{
			block();
		}
	}

	@Override
	public boolean done() {
		return false;
	}

}
