"use client";

import { useCallback, useState } from "react";
import ReactFlow, {
  MiniMap,
  Controls,
  Background,
  useNodesState,
  useEdgesState,
  MarkerType,
  Handle,
  Position
} from "reactflow";
import "reactflow/dist/style.css";
import { AlertTriangle, CheckCircle, Activity, Info } from "lucide-react";

// Custom Node for Events
const EventNode = ({ data, selected }: any) => {
  const isFailed = data.type.includes("FAILED") || data.type.includes("TIMEOUT");
  const isCompleted = data.type.includes("COMPLETED") || data.type.includes("SUCCESS");

  return (
    <div className={`px-4 py-3 shadow-lg rounded-xl border-2 transition-all min-w-[220px] 
      ${selected ? "border-indigo-500 scale-105" : (isFailed ? "border-rose-500/50 bg-slate-900" : "border-slate-800 bg-slate-900")}`}
    >
      <Handle type="target" position={Position.Top} className="!bg-slate-500" />
      
      <div className="flex items-center space-x-2 mb-2">
        {isFailed ? <AlertTriangle size={16} className="text-rose-500" /> : 
         isCompleted ? <CheckCircle size={16} className="text-emerald-500" /> : 
         <Activity size={16} className="text-indigo-400" />}
        <div className={`text-xs font-bold ${isFailed ? "text-rose-400" : "text-white"}`}>
          {data.type}
        </div>
      </div>
      
      <div className="text-[10px] text-slate-500 font-mono mb-1">{data.id}</div>
      <div className="text-[10px] text-slate-400 bg-slate-800 px-2 py-0.5 rounded-full inline-block">
        {data.service}
      </div>

      <Handle type="source" position={Position.Bottom} className="!bg-slate-500" />
    </div>
  );
};

const nodeTypes = { eventNode: EventNode };

const initialNodes = [
  { id: "evt-1", type: "eventNode", position: { x: 250, y: 50 }, data: { id: "evt-1", type: "ORDER_CREATED", service: "order-service", correlation: "ORD-101", timestamp: "10:02:30" } },
  { id: "evt-2", type: "eventNode", position: { x: 250, y: 150 }, data: { id: "evt-2", type: "PAYMENT_STARTED", service: "payment-service", correlation: "ORD-101", timestamp: "10:02:30" } },
  { id: "evt-3", type: "eventNode", position: { x: 250, y: 250 }, data: { id: "evt-3", type: "PAYMENT_SUCCESS", service: "payment-service", correlation: "ORD-101", timestamp: "10:02:31" } },
  { id: "evt-4", type: "eventNode", position: { x: 250, y: 350 }, data: { id: "evt-4", type: "INVENTORY_RESERVATION_REQUESTED", service: "inventory-service", correlation: "ORD-101", timestamp: "10:02:31" } },
  { id: "evt-5", type: "eventNode", position: { x: 250, y: 450 }, data: { id: "evt-5", type: "INVENTORY_TIMEOUT", service: "inventory-service", correlation: "ORD-101", timestamp: "10:02:32" } },
  { id: "evt-6", type: "eventNode", position: { x: 250, y: 550 }, data: { id: "evt-6", type: "ORDER_FAILED", service: "order-service", correlation: "ORD-101", timestamp: "10:02:32" } }
];

const initialEdges = [
  { id: "e1-2", source: "evt-1", target: "evt-2", animated: true, markerEnd: { type: MarkerType.ArrowClosed, color: "#64748b" }, style: { stroke: "#64748b" } },
  { id: "e2-3", source: "evt-2", target: "evt-3", animated: true, markerEnd: { type: MarkerType.ArrowClosed, color: "#64748b" }, style: { stroke: "#64748b" } },
  { id: "e3-4", source: "evt-3", target: "evt-4", animated: true, markerEnd: { type: MarkerType.ArrowClosed, color: "#64748b" }, style: { stroke: "#64748b" } },
  { id: "e4-5", source: "evt-4", target: "evt-5", animated: true, markerEnd: { type: MarkerType.ArrowClosed, color: "#f43f5e" }, style: { stroke: "#f43f5e" } },
  { id: "e5-6", source: "evt-5", target: "evt-6", animated: true, markerEnd: { type: MarkerType.ArrowClosed, color: "#f43f5e" }, style: { stroke: "#f43f5e" } },
];

export default function CausalGraph() {
  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);
  const [selectedEvent, setSelectedEvent] = useState<any>(null);

  const onNodeClick = useCallback((event: any, node: any) => {
    setSelectedEvent(node.data);
    
    // Highlight paths
    setEdges((eds) =>
      eds.map((e) => {
        if (e.source === node.id || e.target === node.id) {
          e.animated = true;
          e.style = { ...e.style, stroke: '#818cf8', strokeWidth: 2 };
        } else {
          e.animated = false;
          e.style = { ...e.style, stroke: '#334155', strokeWidth: 1 };
        }
        return e;
      })
    );
  }, [setEdges]);

  return (
    <div className="flex h-full flex-col">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h2 className="text-xl font-bold text-white flex items-center">
            Causal Graph Visualizer
            <span className="ml-3 px-2 py-0.5 bg-indigo-500/10 text-indigo-400 rounded text-xs border border-indigo-500/20">Phase 6</span>
          </h2>
          <p className="text-sm text-slate-400">Directed Acyclic Graph mapping event causality.</p>
        </div>
      </div>

      <div className="flex flex-1 gap-6 overflow-hidden">
        {/* React Flow Graph */}
        <div className="flex-1 bg-slate-950 rounded-2xl border border-slate-800 relative overflow-hidden">
          <ReactFlow
            nodes={nodes}
            edges={edges}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onNodeClick={onNodeClick}
            nodeTypes={nodeTypes}
            fitView
            className="bg-slate-950"
          >
            <Background color="#1e293b" gap={24} />
            <Controls className="!bg-slate-900 !border-slate-800 !fill-white" />
          </ReactFlow>
        </div>

        {/* Causal Inspector Panel */}
        <div className="w-80 bg-slate-900 rounded-2xl border border-slate-800 p-6 overflow-y-auto flex flex-col">
          <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-6">Causal Inspector</h3>
          
          {selectedEvent ? (
            <div className="flex-1 flex flex-col">
              <div className="mb-6">
                <div className={`text-lg font-bold mb-1 ${selectedEvent.type.includes("FAILED") || selectedEvent.type.includes("TIMEOUT") ? "text-rose-400" : "text-white"}`}>
                  {selectedEvent.type}
                </div>
                <div className="text-sm text-slate-400 flex items-center">
                  <Activity size={14} className="mr-2" /> {selectedEvent.service}
                </div>
              </div>

              <div className="space-y-4 flex-1">
                <div className="bg-slate-950 rounded-lg p-3 border border-slate-800">
                  <div className="text-[10px] text-slate-500 uppercase mb-1 font-bold">Correlation ID</div>
                  <div className="font-mono text-sm text-white">{selectedEvent.correlation}</div>
                </div>

                <div className="bg-slate-950 rounded-lg p-3 border border-slate-800 relative overflow-hidden">
                  <div className="absolute top-0 left-0 w-1 h-full bg-indigo-500"></div>
                  <div className="text-[10px] text-slate-500 uppercase mb-1 font-bold">Causation (Root)</div>
                  <div className="font-mono text-xs text-slate-300">
                    {selectedEvent.id === "evt-1" ? "none (Trigger)" : "evt-3 (PAYMENT_SUCCESS)"}
                  </div>
                </div>

                <div className="bg-slate-950 rounded-lg p-3 border border-slate-800 relative overflow-hidden">
                  <div className="absolute top-0 left-0 w-1 h-full bg-rose-500"></div>
                  <div className="text-[10px] text-slate-500 uppercase mb-1 font-bold">Effects (Caused Events)</div>
                  <div className="font-mono text-xs text-slate-300">
                    {selectedEvent.id === "evt-6" ? "none" : "evt-6 (ORDER_FAILED)"}
                  </div>
                </div>
              </div>

              <div className="mt-8 space-y-3 pt-6 border-t border-slate-800">
                <button className="w-full py-2 bg-slate-800 hover:bg-slate-700 text-white rounded-lg text-sm font-medium transition-colors border border-slate-700">
                  Trace Causes
                </button>
                <button className="w-full py-2 bg-slate-800 hover:bg-slate-700 text-white rounded-lg text-sm font-medium transition-colors border border-slate-700">
                  Trace Effects
                </button>
                <button className="w-full py-2 bg-indigo-600 hover:bg-indigo-500 shadow-lg shadow-indigo-500/20 text-white rounded-lg text-sm font-medium transition-colors mt-4">
                  Fork From Here
                </button>
              </div>
            </div>
          ) : (
            <div className="text-center text-slate-500 mt-20 flex flex-col items-center">
              <Info size={32} className="mb-4 opacity-50" />
              <p>Select a node to inspect its causal chain.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
