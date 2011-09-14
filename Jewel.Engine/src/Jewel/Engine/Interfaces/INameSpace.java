package Jewel.Engine.Interfaces;

import java.util.*;

public interface INameSpace
	extends IJewelBase
{
		String Storage();

		String getName();
        INameSpace getParent();
        String getAssembly();
        String getStaticClass();
        String getLoginMethod();
        void DoLogin(UUID pidUser, boolean pbNested);
}
