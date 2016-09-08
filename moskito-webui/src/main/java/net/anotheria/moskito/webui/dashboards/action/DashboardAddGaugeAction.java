package net.anotheria.moskito.webui.dashboards.action;

import net.anotheria.maf.action.ActionCommand;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.maf.action.CommandRedirect;
import net.anotheria.maf.bean.FormBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This action creates a new accumulator.
 * @author lrosenberg
 */
public class DashboardAddGaugeAction extends BaseDashboardAction {
	@Override
	public ActionCommand execute(ActionMapping mapping, FormBean formBean, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String gaugeName = request.getParameter("pName");
		String[] dashboardsName = request.getParameterValues("pDashboards");

		for(String dashboard : dashboardsName) {
			getDashboardAPI().addGaugeToDashboard(dashboard, gaugeName);
		}

		CommandRedirect commandRedirect =  mapping.redirect().addParameter("lo", ShowDashboardAction.LastOperation.gadd.name());
		if (dashboardsName.length == 1) {
			commandRedirect = commandRedirect.addParameter("dashboard", dashboardsName[0]);
		}
		return commandRedirect;
	}
}
