"use client";

import { useState } from "react";
import { ArrowRight, GitBranch, Play, RotateCcw, Box, CreditCard, ShoppingCart, Truck } from "lucide-react";
import Link from "next/link";

type EventNode = {
  id: string;
  type: string;
  service: string;
  timestamp: string;
  state: any;
};

const MAIN_EVENTS: EventNode[] = [
  {
    id: "evt-100",
    type: "ORDER_CREATED",
    service: "Order Service",
    timestamp: "10:00:00",
    state: { order: { id: "ORD-101", status: "CREATED" } },
  },
  {
    id: "evt-101",
    type: "PAYMENT_STARTED",
    service: "Payment Service",
    timestamp: "10:00:01",
    state: { order: { id: "ORD-101", status: "CREATED" }, payment: { status: "STARTED" } },
  },
  {
    id: "evt-102",
    type: "PAYMENT_SUCCESS",
    service: "Payment Service",
    timestamp: "10:00:02",
    state: { order: { id: "ORD-101", status: "CREATED" }, payment: { status: "SUCCESS" } },
  },
  {
    id: "evt-103",
    type: "INVENTORY_RESERVED",
    service: "Inventory Service",
    timestamp: "10:00:03",
    state: { order: { id: "ORD-101", status: "CREATED" }, payment: { status: "SUCCESS" }, inventory: { status: "RESERVED" } },
  },
];

const EXP_EVENTS: EventNode[] = [
  {
    id: "evt-202",
    type: "PAYMENT_FAILED",
    service: "Payment Service",
    timestamp: "10:00:02",
    state: { order: { id: "ORD-101", status: "CREATED" }, payment: { status: "FAILED" } },
  },
  {
    id: "evt-203",
    type: "ORDER_CANCELLED",
    service: "Order Service",
    timestamp: "10:00:04",
    state: { order: { id: "ORD-101", status: "CANCELLED" }, payment: { status: "FAILED" }, inventory: { status: "RELEASED" } },
  },
];

export default function TimelineView({ params }: { params: { id: string } }) {
  const [selectedEvent, setSelectedEvent] = useState<EventNode | null>(MAIN_EVENTS[2]);
  const [showForkModal, setShowForkModal] = useState(false);
  const isExperiment = params.id === "exp-001";

  return (
    <div className="flex h-full flex-col">
      {/* Header */}
      <div className="flex items-center justify-between mb-8 pb-4 border-b border-slate-800">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center">
            {isExperiment ? "Payment Failure Experiment" : "E-Commerce Demo"}
            {isExperiment && (
              <span className="ml-3 px-2 py-0.5 bg-amber-500/10 text-amber-400 rounded text-xs border border-amber-500/20">
                EXP-001
              </span>
            )}
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            {isExperiment ? "Forked from E-Commerce Demo at Event #101" : "Main execution timeline"}
          </p>
        </div>
        <div className="flex space-x-3">
          {!isExperiment && (
            <button
              onClick={() => setShowForkModal(true)}
              className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors text-sm font-medium"
            >
              <GitBranch size={16} />
              <span>Fork Timeline</span>
            </button>
          )}
          <button className="flex items-center space-x-2 px-4 py-2 bg-slate-800 hover:bg-slate-700 text-white rounded-lg transition-colors text-sm font-medium">
            <RotateCcw size={16} />
            <span>Replay</span>
          </button>
          <button className="flex items-center space-x-2 px-4 py-2 bg-slate-800 hover:bg-slate-700 text-white rounded-lg transition-colors text-sm font-medium">
            <Play size={16} />
            <span>Play</span>
          </button>
        </div>
      </div>

      <div className="flex flex-1 gap-8 overflow-hidden">
        {/* Timeline Visualizer */}
        <div className="flex-1 overflow-x-auto pb-8">
          <div className="min-w-max">
            {/* Visual branching logic */}
            <div className="relative pt-12 pb-24 px-12">
              {/* Main Line */}
              <div className="absolute top-1/2 left-0 w-full h-1 bg-slate-800 -translate-y-1/2 z-0 rounded-full"></div>

              {/* Events */}
              <div className="relative z-10 flex items-center justify-between gap-24">
                {(isExperiment ? MAIN_EVENTS.slice(0, 2).concat(EXP_EVENTS) : MAIN_EVENTS).map((evt, idx) => (
                  <div key={evt.id} className="flex flex-col items-center group cursor-pointer" onClick={() => setSelectedEvent(evt)}>
                    <div className="mb-4 text-xs font-mono text-slate-500">{evt.timestamp}</div>
                    <div className={`w-6 h-6 rounded-full flex items-center justify-center transition-all ${selectedEvent?.id === evt.id ? 'bg-indigo-500 ring-4 ring-indigo-500/20' : 'bg-slate-700 group-hover:bg-slate-500'}`}>
                      <div className="w-2 h-2 bg-white rounded-full"></div>
                    </div>
                    <div className="mt-4 text-center">
                      <div className={`text-sm font-bold ${selectedEvent?.id === evt.id ? 'text-indigo-400' : 'text-slate-300'}`}>{evt.type}</div>
                      <div className="text-xs text-slate-500 mt-1">{evt.service}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
            
            {/* Branch visualization if viewing experiment */}
            {isExperiment && (
               <div className="mt-8 border-t border-dashed border-slate-800 pt-8 text-center text-sm text-slate-500">
                 <p>Diverged from Main Timeline</p>
                 <Link href="/timeline/demo" className="text-indigo-400 hover:underline mt-2 inline-block">View Parent Timeline &rarr;</Link>
               </div>
            )}
          </div>
        </div>

        {/* State Inspector Panel */}
        <div className="w-80 border-l border-slate-800 pl-8 overflow-y-auto">
          {selectedEvent ? (
            <div>
              <div className="mb-6">
                <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Selected Event</h3>
                <div className="bg-slate-900 rounded-lg p-4 border border-slate-800">
                  <div className="text-indigo-400 font-mono text-xs mb-1">{selectedEvent.id}</div>
                  <div className="font-bold text-white mb-2">{selectedEvent.type}</div>
                  <div className="text-sm text-slate-400 flex items-center">
                    <Activity size={14} className="mr-2" /> {selectedEvent.service}
                  </div>
                  <div className="text-sm text-slate-400 flex items-center mt-2">
                    <Clock size={14} className="mr-2" /> {selectedEvent.timestamp}
                  </div>
                </div>
              </div>

              <div>
                <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-4 flex items-center justify-between">
                  <span>Reconstructed State</span>
                  <span className="text-[10px] bg-slate-800 px-2 py-0.5 rounded text-slate-400">DETERMINISTIC</span>
                </h3>
                
                <div className="space-y-4">
                  {selectedEvent.state.order && (
                    <div className="bg-slate-900/50 rounded-lg p-4 border border-slate-800/50">
                      <div className="flex items-center text-slate-300 mb-3 text-sm font-medium">
                        <ShoppingCart size={16} className="mr-2 text-cyan-400" /> Order State
                      </div>
                      <div className="space-y-2 text-sm">
                        <div className="flex justify-between"><span className="text-slate-500">ID</span><span className="font-mono text-white">{selectedEvent.state.order.id}</span></div>
                        <div className="flex justify-between">
                          <span className="text-slate-500">Status</span>
                          <span className={`font-mono ${selectedEvent.state.order.status === 'CANCELLED' ? 'text-rose-400' : 'text-emerald-400'}`}>
                            {selectedEvent.state.order.status}
                          </span>
                        </div>
                      </div>
                    </div>
                  )}

                  {selectedEvent.state.payment && (
                    <div className="bg-slate-900/50 rounded-lg p-4 border border-slate-800/50">
                      <div className="flex items-center text-slate-300 mb-3 text-sm font-medium">
                        <CreditCard size={16} className="mr-2 text-indigo-400" /> Payment State
                      </div>
                      <div className="space-y-2 text-sm">
                        <div className="flex justify-between">
                          <span className="text-slate-500">Status</span>
                          <span className={`font-mono ${selectedEvent.state.payment.status === 'FAILED' ? 'text-rose-400' : 'text-emerald-400'}`}>
                            {selectedEvent.state.payment.status}
                          </span>
                        </div>
                      </div>
                    </div>
                  )}

                  {selectedEvent.state.inventory && (
                    <div className="bg-slate-900/50 rounded-lg p-4 border border-slate-800/50">
                      <div className="flex items-center text-slate-300 mb-3 text-sm font-medium">
                        <Box size={16} className="mr-2 text-amber-400" /> Inventory State
                      </div>
                      <div className="space-y-2 text-sm">
                        <div className="flex justify-between">
                          <span className="text-slate-500">Status</span>
                          <span className="font-mono text-emerald-400">
                            {selectedEvent.state.inventory.status}
                          </span>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ) : (
            <div className="text-center text-slate-500 mt-20">Select an event to view state</div>
          )}
        </div>
      </div>

      {/* Fork Modal */}
      {showForkModal && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-700 rounded-2xl w-full max-w-md p-6 shadow-2xl">
            <h2 className="text-xl font-bold text-white mb-6">Create Experiment (Fork)</h2>
            <div className="space-y-4 mb-8">
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-1">Experiment Name</label>
                <input type="text" defaultValue="Payment Failure Experiment" className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-1">Fork From Event</label>
                <div className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-slate-300 font-mono text-sm">
                  evt-101 (PAYMENT_STARTED)
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2 mt-4">Simulate Alternate Outcome</label>
                <select className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500">
                  <option>Payment Service: FAILED</option>
                  <option>Inventory Service: TIMEOUT</option>
                  <option>Network: DELAY 500ms</option>
                </select>
              </div>
            </div>
            <div className="flex justify-end space-x-3">
              <button onClick={() => setShowForkModal(false)} className="px-4 py-2 text-slate-300 hover:text-white font-medium transition-colors">Cancel</button>
              <Link href="/timeline/exp-001" onClick={() => setShowForkModal(false)}>
                <button className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium rounded-lg shadow-lg shadow-indigo-500/20 transition-all">
                  Run Simulation
                </button>
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
