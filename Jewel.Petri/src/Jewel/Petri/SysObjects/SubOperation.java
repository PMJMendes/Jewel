package Jewel.Petri.SysObjects;

import java.io.Serializable;
import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;

public abstract class SubOperation
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	public abstract void LongDesc(StringBuilder pstrResult, String pstrLineBreak);
	public abstract void RunSubOp(SQLServer pdb, UUID pidOwner) throws JewelPetriException;
	public abstract void UndoDesc(StringBuilder pstrResult, String pstrLineBreak);
	public abstract void UndoLongDesc(StringBuilder pstrResult, String pstrLineBreak);
	public abstract void UndoSubOp(SQLServer pdb, UUID pidOwner) throws JewelPetriException;
}
