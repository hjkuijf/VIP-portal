/* Copyright CNRS-CREATIS
 *
 * Rafael Silva
 * rafael.silva@creatis.insa-lyon.fr
 * http://www.rafaelsilva.com
 *
 * This software is a grid-enabled data-driven workflow manager and editor.
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
package fr.insalyon.creatis.vip.datamanager.client.view.operation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import fr.insalyon.creatis.vip.core.client.view.ModalWindow;
import fr.insalyon.creatis.vip.datamanager.client.DataManagerConstants;
import fr.insalyon.creatis.vip.datamanager.client.bean.PoolOperation;
import fr.insalyon.creatis.vip.datamanager.client.bean.PoolOperation.Status;
import fr.insalyon.creatis.vip.datamanager.client.bean.PoolOperation.Type;
import fr.insalyon.creatis.vip.datamanager.client.rpc.DataManagerService;
import fr.insalyon.creatis.vip.datamanager.client.rpc.DataManagerServiceAsync;

/**
 *
 * @author Rafael Silva
 */
public class OperationBoxLayout extends HLayout {

    private ModalWindow modal;
    private PoolOperation operation;

    public OperationBoxLayout(ModalWindow modal, PoolOperation operation) {

        this.modal = modal;
        this.operation = operation;

        this.setMembersMargin(2);
        this.setWidth100();
        this.setHeight(40);
        this.setBackgroundColor("#FFFFFF");

        configureImageLayout();
        configureMainLayout();
        configureActionLayout();
    }

    private void configureImageLayout() {

        VLayout imgLayout = new VLayout();
        imgLayout.setPadding(2);
        imgLayout.setWidth(40);
        imgLayout.setHeight(45);
        imgLayout.setAlign(Alignment.CENTER);

        Img icon;

        if (operation.getType() == Type.Download) {
            icon = new Img(DataManagerConstants.OP_DOWNLOAD, 32, 32);
            icon.setPrompt("Download");
            if (operation.getStatus() == Status.Done) {
                icon.setCursor(Cursor.HAND);
                icon.addClickHandler(new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        download();
                    }
                });
            }
        } else {
            icon = new Img(DataManagerConstants.OP_UPLOAD, 32, 32);
            icon.setPrompt("Upload");
        }
        imgLayout.addMember(icon);
        this.addMember(imgLayout);
    }

    private void configureMainLayout() {

        VLayout mainLayout = new VLayout(2);
        mainLayout.setWidth("*");
        mainLayout.setHeight(45);
        mainLayout.setAlign(Alignment.CENTER);

        String source = operation.getSource();
        String message = operation.getType() == Type.Download
                ? source : operation.getDest() + "/"
                + source;

        Label messageLabel = new Label(message);
        messageLabel.setHeight(15);
        mainLayout.addMember(messageLabel);

        if (operation.getStatus() == Status.Queued || operation.getStatus() == Status.Rescheduled) {
            Label waitingLabel = new Label(
                    "<font color=\"#D9D509\">Waiting</font> <font color=\"#666666\">- "
                    + operation.getParsedRegistration() + "</font>");
            waitingLabel.setHeight(15);
            mainLayout.addMember(waitingLabel);

        } else if (operation.getStatus() == Status.Done) {
            Label completedLabel = new Label(
                    "<font color=\"#666666\">Completed - "
                    + operation.getParsedRegistration() + "</font>");
            completedLabel.setHeight(15);
            mainLayout.addMember(completedLabel);

        } else if (operation.getStatus() == Status.Failed) {
            Label failedLabel = new Label(
                    "<font color=\"#BF153F\">Failed</font> <font color=\"#666666\">- "
                    + operation.getParsedRegistration() + "</font>");
            failedLabel.setHeight(15);
            mainLayout.addMember(failedLabel);

        } else if (operation.getStatus() == Status.Running) {
            Label failedLabel = new Label(
                    "<font color=\"#1B9406\">Running</font> <font color=\"#666666\">- "
                    + operation.getParsedRegistration() + "</font>");
            failedLabel.setHeight(15);
            mainLayout.addMember(failedLabel);
//            ProgressBar progressBar = new ProgressBar(10, "#D3E0F2", 30);
//            mainLayout.addMember(progressBar);
        }

        this.addMember(mainLayout);
    }

    private void configureActionLayout() {

        VLayout actionLayout = new VLayout(5);
        actionLayout.setHeight(45);
        actionLayout.setWidth(30);
        actionLayout.setAlign(VerticalAlignment.TOP);

        Img removeImg = new Img(DataManagerConstants.OP_ICON_CLEAR, 16, 16);
        removeImg.setCursor(Cursor.HAND);
        removeImg.setPrompt("Remove");
        removeImg.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                remove();
            }
        });
        actionLayout.addMember(removeImg);

        if (operation.getType() == Type.Download && operation.getStatus() == Status.Done) {
            Img downloadImg = new Img(DataManagerConstants.OP_ICON_DOWNLOAD, 16, 16);
            downloadImg.setCursor(Cursor.HAND);
            downloadImg.setPrompt("Download");
            downloadImg.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    download();
                }
            });
            actionLayout.addMember(downloadImg);
        }

        this.addMember(actionLayout);
    }

    private void remove() {

        SC.confirm("Do you want to remove this operation?", new BooleanCallback() {

            public void execute(Boolean value) {
                if (value != null && value) {
                    if (operation.getStatus() != Status.Running) {
                        DataManagerServiceAsync service = DataManagerService.Util.getInstance();
                        AsyncCallback<Void> callback = new AsyncCallback<Void>() {

                            public void onFailure(Throwable caught) {
                                modal.hide();
                                SC.warn("Unable to remove operation:<br />" + caught.getMessage());
                            }

                            public void onSuccess(Void result) {
                                modal.hide();
                                OperationLayout.getInstance().loadData();
                            }
                        };
                        modal.show("Removing operation...", true);
                        service.removeOperationById(operation.getId(), callback);
                    }
                }
            }
        });
    }

    private void download() {

        Window.open(GWT.getModuleBaseURL() + "/filedownloadservice?operationid="
                + operation.getId(), "", "");
    }
}