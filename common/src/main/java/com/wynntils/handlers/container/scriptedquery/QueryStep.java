/*
 * Copyright © Wynntils 2023-2024.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.handlers.container.scriptedquery;

import com.wynntils.core.components.Handlers;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.container.ContainerQueryException;
import com.wynntils.handlers.container.ContainerQueryStep;
import com.wynntils.handlers.container.type.ContainerAction;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.handlers.container.type.ContainerContentChangeType;
import com.wynntils.handlers.container.type.ContainerContentVerification;
import com.wynntils.handlers.container.type.ContainerPredicate;
import com.wynntils.handlers.container.type.ContainerVerification;
import com.wynntils.utils.wynn.ContainerUtils;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import org.lwjgl.glfw.GLFW;

public class QueryStep {
    // We should never get to MenuOpenedEvent
    private static final ContainerVerification EXPECT_SAME_MENU = (title, type) -> false;
    private static final ContainerContentVerification WAIT_FOR_SET_CONTENT =
            (container, changes, changeType) -> changeType == ContainerContentChangeType.SET_CONTENT;
    private static final ContainerAction IGNORE_INCOMING_CONTAINER = c -> {};

    private final ContainerPredicate startAction;
    private ContainerVerification verification = EXPECT_SAME_MENU;
    private ContainerContentVerification contentVerification = WAIT_FOR_SET_CONTENT;
    private ContainerAction handleContent = IGNORE_INCOMING_CONTAINER;

    protected QueryStep(ContainerPredicate startAction) {
        this.startAction = startAction;
    }

    protected QueryStep(QueryStep queryStep) {
        this.startAction = queryStep.startAction;
        this.verification = queryStep.verification;
        this.contentVerification = queryStep.contentVerification;
        this.handleContent = queryStep.handleContent;
    }

    // region Builder API actions

    public static QueryStep useItemInHotbar(int slotNum) {
        return new QueryStep((container) -> ContainerUtils.openInventory(slotNum));
    }

    public static QueryStep clickOnSlot(int slotNum) {
        return new QueryStep(container -> {
            ContainerUtils.clickOnSlot(
                    slotNum, container.containerId(), GLFW.GLFW_MOUSE_BUTTON_LEFT, container.items());
            return true;
        });
    }

    public static QueryStep clickOnSlot(int slotNum, Supplier<Integer> mouseButtonSupplier) {
        return new QueryStep(container -> {
            ContainerUtils.clickOnSlot(slotNum, container.containerId(), mouseButtonSupplier.get(), container.items());
            return true;
        });
    }

    public static QueryStep clickOnMatchingSlot(int slotNum, Item expectedItemType, StyledText expectedItemName) {
        return new QueryStep(container -> {
            if (!ScriptedContainerQuery.containerHasSlot(container, slotNum, expectedItemType, expectedItemName))
                throw new ContainerQueryException("Cannot find matching slot");

            ContainerUtils.clickOnSlot(
                    slotNum, container.containerId(), GLFW.GLFW_MOUSE_BUTTON_LEFT, container.items());
            return true;
        });
    }

    public static QueryStep sendCommand(String command) {
        return new QueryStep(container -> {
            Handlers.Command.queueCommand(command);
            return true;
        });
    }

    public QueryStep expectContainerTitle(String regExp) {
        this.verification = (title, type) -> title.getString().matches(regExp);
        return this;
    }

    public QueryStep verifyContentChange(ContainerContentVerification verification) {
        this.contentVerification = verification;
        return this;
    }

    public QueryStep processIncomingContainer(ContainerAction action) {
        this.handleContent = action;
        return this;
    }

    // endregion

    // region ScriptedContainerQuery support

    ContainerVerification getVerification() {
        return verification;
    }

    ContainerContentVerification getContentVerification() {
        return contentVerification;
    }

    ContainerAction getHandleContent() {
        return handleContent;
    }

    boolean startStep(ScriptedContainerQuery query, ContainerContent container) throws ContainerQueryException {
        return startAction.execute(container);
    }

    ContainerQueryStep getNextStep(ScriptedContainerQuery query) {
        // Go to next step, if any
        if (!query.popOneStep()) return null;

        return query;
    }

    // endregion
}
