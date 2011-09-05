package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.Interfaces.IJewelBase;
import Jewel.Petri.SysObjects.JewelPetriException;

public interface INode
	extends IJewelBase
{
	public UUID GetControllerID();
	public IController GetController() throws JewelPetriException;
	public void PrepCount();
	public void TryDecCount();
	public boolean CheckCount();
	public void DecCount() throws JewelPetriException;
	public void IncCount() throws JewelPetriException;
	public void DoSafeSave() throws JewelPetriException;
	public void RollbackSafeSave();
	public void CommitSafeSave(SQLServer pdb) throws JewelPetriException;
}
