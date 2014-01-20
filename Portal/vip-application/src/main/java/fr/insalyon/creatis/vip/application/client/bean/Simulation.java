/* Copyright CNRS-CREATIS
 *
 * Rafael Ferreira da Silva
 * rafael.silva@creatis.insa-lyon.fr
 * http://www.rafaelsilva.com
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.insalyon.creatis.vip.application.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;
import fr.insalyon.creatis.vip.application.client.view.monitor.SimulationStatus;
import java.util.Date;

/**
 *
 * @author Rafael Ferreira da Silva
 */
public class Simulation implements IsSerializable {

    private String id;
    private String applicationName;
    private String applicationVersion;
    private String applicationClass;
    private String simulationName;
    private String userName;
    private Date date;
    private SimulationStatus status;

    public Simulation() {
    }

    public Simulation(String application, String applicationVersion,
            String applicationClass, String id, String userName, Date date, 
            String simulationName, String status) {

        this.applicationName = application;
        this.applicationVersion = applicationVersion;
        this.applicationClass = applicationClass;
        this.id = id;
        this.userName = userName;
        this.date = date;
        this.simulationName = simulationName;
        this.status = SimulationStatus.valueOf(status);
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }
    
    public String getApplicationClass() {
        return applicationClass;
    }

    public Date getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public String getID() {
        return id;
    }

    public SimulationStatus getStatus() {
        return status;
    }

    public void setStatus(SimulationStatus status) {
        this.status = status;
    }

    public String getSimulationName() {
        return simulationName;
    }

    @Override
    public String toString() {
        return applicationName + "\n" + id + "\n" + userName + "\n" + date;
    }
}